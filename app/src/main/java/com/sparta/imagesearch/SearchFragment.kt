package com.sparta.imagesearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sparta.imagesearch.data.RecyclerViewImage
import com.sparta.imagesearch.databinding.FragmentSearchBinding
import com.sparta.imagesearch.recyclerView.GridSpacingItemDecoration
import com.sparta.imagesearch.recyclerView.RecyclerViewImageAdapter
import com.sparta.imagesearch.retrofit.ImageResponse
import com.sparta.imagesearch.retrofit.SearchClient
import com.sparta.imagesearch.util.fromDpToPx
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private val TAG = "SearchFragment"

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: RecyclerViewImageAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initImageRecyclerView()
        setSearchButtonOnClickListener()
    }

    private fun initImageRecyclerView() {
        imageAdapter = RecyclerViewImageAdapter(mutableListOf<RecyclerViewImage>())
        binding.recyclerviewImage.run {
            adapter = imageAdapter
            addItemDecoration(GridSpacingItemDecoration(2, 16f.fromDpToPx()))
        }
    }

    private fun setSearchButtonOnClickListener() {
        binding.btnSearch.setOnClickListener {
            hideKeyboard(it)

            val keyword = binding.etSearch.text.toString()
            var imageResponse: ImageResponse? = null
            lifecycleScope.launch { communicateImageSearchNetwork(keyword) }
        }
    }
    private fun hideKeyboard(view:View){
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    private suspend fun communicateImageSearchNetwork(query: String) {
        var imageResponse: ImageResponse? = null
        val job = lifecycleScope.launch {
            try {
                imageResponse = SearchClient.searchNetWork.getImage(query)
            } catch (e: Exception) {
                e.printStackTrace()
                cancel()
            }
        }
        job.join()

        val newDataset = imageResponseToDataset(imageResponse)
        updateImageRecyclerView(newDataset)
    }

    private fun updateImageRecyclerView(newDataset: MutableList<RecyclerViewImage>) {
        imageAdapter.changeDataset(newDataset)
    }

    private fun imageResponseToDataset(imageResponse: ImageResponse?): MutableList<RecyclerViewImage> {
        val newDataset = mutableListOf<RecyclerViewImage>()
        imageResponse?.documents?.forEach {
            newDataset.add(RecyclerViewImage.createFromImageDocument(it))
        }
        return newDataset
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}