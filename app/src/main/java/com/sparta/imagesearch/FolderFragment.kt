package com.sparta.imagesearch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.sparta.imagesearch.data.FolderManager
import com.sparta.imagesearch.data.Item
import com.sparta.imagesearch.databinding.FragmentFolderBinding
import com.sparta.imagesearch.recyclerView.FolderAdapter
import com.sparta.imagesearch.recyclerView.GridSpacingItemDecoration
import com.sparta.imagesearch.recyclerView.ItemAdapter
import com.sparta.imagesearch.recyclerView.OnFolderClickListener
import com.sparta.imagesearch.recyclerView.OnItemClickListener
import com.sparta.imagesearch.util.fromDpToPx

class FolderFragment : Fragment(), OnItemClickListener, OnFolderClickListener {
    private val TAG = "FolderFragment"

    private var _binding: FragmentFolderBinding? = null
    private val binding get() = _binding!!

    private lateinit var folderAdapter: FolderAdapter
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFolderRecyclerView()
        initMoreButton()
        initMoreLayout()

        initImageRecyclerView()
    }

    private fun initFolderRecyclerView() {
        folderAdapter = FolderAdapter(FolderManager.getFolders())
        folderAdapter.onFolderClickListener = this@FolderFragment
        binding.recyclerViewFolder.adapter = folderAdapter
    }

    override fun onClick(folderId: String) {
        FolderManager.setSelectedFolderId(folderId)
        itemAdapter.changeDataset(
            getFolderDataset(FolderManager.getSelectedFolderId())
        )
    }

    private fun getFolderDataset(folderId: String) = MainActivity.savedItems.filter {
        it.folder?.id == folderId
    }.toMutableList()

    private fun initMoreButton() {
        binding.ivMore.setOnClickListener {
            binding.layoutMore.isVisible = !binding.layoutMore.isVisible
        }
    }
    private fun initMoreLayout() {
        binding.tvMoreAdd.setOnClickListener {
            //TODO 폴더 생성 다이얼로그 표시
        }
        binding.tvMoreDelete.setOnClickListener {
            //TODO 폴더 삭제 다이얼로그 표시
        }
    }

    private fun initImageRecyclerView() {
        itemAdapter = ItemAdapter(getFolderDataset(FolderManager.getSelectedFolderId()))
        itemAdapter.onItemClickListener = this@FolderFragment
        binding.recyclerviewImage.run {
            adapter = itemAdapter
            addItemDecoration(GridSpacingItemDecoration(2, 16f.fromDpToPx()))
        }
    }

    override fun onImageClick(item: Item) {
        //TODO("Not yet implemented")
    }

    override fun onHeartClick(position: Int, item: Item) {
        //TODO("Not yet implemented")
        item.unsaveItem()
        itemAdapter.notifyItemRemoved(position)
    }

    override fun onHeartLongClick(item: Item) {
        //TODO("Not yet implemented")
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        itemAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}