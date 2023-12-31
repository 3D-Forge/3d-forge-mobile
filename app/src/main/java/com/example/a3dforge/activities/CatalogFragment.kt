package com.example.a3dforge.activities

import OkHttpConfig
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.adapter.ProductAdapter
import com.example.a3dforge.base.NDSpinner
import com.example.a3dforge.factories.CatalogSearchViewModelFactory
import com.example.a3dforge.models.CatalogSearchViewModel
import com.example.a3dforge.models.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CatalogFragment : Fragment() {

    private lateinit var rcView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var filterButton: Button
    private lateinit var resetFiltersButt: Button
    private lateinit var addNewModelButton: Button
    private lateinit var adapter: ProductAdapter
    private lateinit var sortSpinner: NDSpinner
    private lateinit var sortIndexImageView: ImageView

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var loadingJob: Job
    private lateinit var handler: Handler

    private lateinit var catalogSearchViewModel: CatalogSearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        handler = Handler(Looper.getMainLooper())

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        filterButton = view.findViewById(R.id.filterButton)
        resetFiltersButt = view.findViewById(R.id.resetFiltersButt)
        addNewModelButton = view.findViewById(R.id.addNewModelButton)
        rcView = view.findViewById(R.id.rcView)
        sortIndexImageView = view.findViewById(R.id.sortIndexImageView)

        val okHttpConfig = OkHttpConfig

        catalogSearchViewModel = ViewModelProvider(this, CatalogSearchViewModelFactory(okHttpConfig))
            .get(CatalogSearchViewModel::class.java)

        catalogSearchViewModel.profileResult.observe(viewLifecycleOwner, Observer { result  ->
            result.second?.let {
                val list = result.second!!.data.items
                adapter.submitList(list)
            }
        })

        loadingJob = lifecycleScope.launch {
            rcView.visibility = View.GONE

            startLoadingAnimation()

            loadCatalogData()

            stopLoadingAnimation()

            showRecyclerView()
        }

        sortSpinner = view.findViewById(R.id.sortSpinner)
        val sortItems = listOf("За назвою", "За ціною", "За рейтингом")
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, sortItems)
        sortSpinner.adapter = arrayAdapter

        sortIndexImageView.rotation = 180F

        sortSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            var sortDirectionAsc = true

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (position == 0) {
                    sharedViewModel.filterParameters.sortParameter = "name"

                    sharedViewModel.filterParameters.sortDirection =
                        if (sortDirectionAsc) {
                            "asc"
                        } else {
                            "desc"
                        }

                    sortDirectionAsc = !sortDirectionAsc
                }
                if (position == 1) {
                    sharedViewModel.filterParameters.sortParameter = "price"

                    sharedViewModel.filterParameters.sortDirection =
                        if (sortDirectionAsc) {
                            "asc"
                        } else {
                            "desc"
                        }

                    sortDirectionAsc = !sortDirectionAsc
                }
                if (position == 2) {
                    sharedViewModel.filterParameters.sortParameter = "rating"

                    sharedViewModel.filterParameters.sortDirection =
                        if (sortDirectionAsc) {
                            "asc"
                        } else {
                            "desc"
                        }

                    sortDirectionAsc = !sortDirectionAsc
                }

                loadingJob = lifecycleScope.launch {
                    rcView.visibility = View.GONE

                    startLoadingAnimation()

                    loadCatalogData()

                    stopLoadingAnimation()

                    showRecyclerView()
                }

                sortIndexImageView.rotation += 180F
            }


            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        })

        resetFiltersButt.setOnClickListener{
            sharedViewModel.filterParameters.q = null
            sharedViewModel.filterParameters.categories = null
            sharedViewModel.filterParameters.pageSize = null
            sharedViewModel.filterParameters.page = null
            sharedViewModel.filterParameters.author = null
            sharedViewModel.filterParameters.keywords = null
            sharedViewModel.filterParameters.rating = null
            sharedViewModel.filterParameters.minPrice = null
            sharedViewModel.filterParameters.maxPrice = null
            searchEditText.text = Editable.Factory.getInstance().newEditable("")
            loadingJob = lifecycleScope.launch {
                rcView.visibility = View.GONE

                startLoadingAnimation()

                loadCatalogData()

                stopLoadingAnimation()

                showRecyclerView()
            }
        }

        addNewModelButton.setOnClickListener{
            val newModelFragment = NewModelFragment()

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, newModelFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        adapter = ProductAdapter(requireActivity())
        val spanCount = 2
        rcView.layoutManager = GridLayoutManager(this.requireContext(), spanCount)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        rcView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacingInPixels, true))

        rcView.adapter = adapter

        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchButton.setOnClickListener {
                    sharedViewModel.filterParameters.q = searchEditText.text.toString()
                    loadingJob = lifecycleScope.launch {
                        rcView.visibility = View.GONE

                        startLoadingAnimation()

                        loadCatalogData()

                        stopLoadingAnimation()

                        showRecyclerView()
                    }
                }
                true
            } else {
                false
            }
        }

        searchButton.setOnClickListener {
            sharedViewModel.filterParameters.q = searchEditText.text.toString()
            loadingJob = lifecycleScope.launch {
                rcView.visibility = View.GONE

                startLoadingAnimation()

                loadCatalogData()

                stopLoadingAnimation()

                showRecyclerView()
            }
        }

        filterButton.setOnClickListener{
            val searchfilterFragment = SearchFilterFragment()

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, searchfilterFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        adapter.setClickListener(object : ProductAdapter.ClickListener {
            override fun onBuyButtonClick(modelId: Int) {
                val modelFragment = ModelFragment()

                val bundle = Bundle()
                bundle.putInt("modelId", modelId)
                modelFragment.arguments = bundle

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, modelFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })

        return view
    }

    private fun showRecyclerView() {
        rcView.visibility = View.VISIBLE
    }

    private suspend fun loadCatalogData() {
        withContext(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    catalogSearchViewModel.getCatalog(
                        sharedViewModel.filterParameters.q.takeIf { it?.isNotBlank() == true },
                        sharedViewModel.filterParameters.categories.takeIf { it?.isNotEmpty() == true },
                        sharedViewModel.filterParameters.keywords?.takeIf { it.isNotEmpty() },
                        sharedViewModel.filterParameters.sortParameter.takeIf { it?.isNotBlank() == true },
                        sharedViewModel.filterParameters.sortDirection.takeIf { it?.isNotBlank() == true },
                        sharedViewModel.filterParameters.minPrice.takeIf { it != null },
                        sharedViewModel.filterParameters.maxPrice.takeIf { it != null },
                        sharedViewModel.filterParameters.rating.takeIf { it != null },
                        sharedViewModel.filterParameters.author.takeIf { it?.isNotBlank() == true },
                        null,
                        null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingJob.cancel()
    }

    override fun onPause() {
        super.onPause()
        loadingJob.cancel()
    }

    private suspend fun startLoadingAnimation() {
        withContext(Dispatchers.Main) {
            loadingProgressBar.visibility = View.VISIBLE
        }
    }

    private suspend fun simulateDataLoading() {
        withContext(Dispatchers.IO) {
            val totalSteps = 100
            for (currentStep in 1..totalSteps) {
                updateProgressBar(currentStep, totalSteps)
                delay(10)
            }
        }
    }

    private suspend fun updateProgressBar(currentStep: Int, totalSteps: Int) {
        withContext(Dispatchers.Main) {
            val progress = (currentStep * 100 / totalSteps)
            loadingProgressBar.progress = progress
        }
    }

    private suspend fun stopLoadingAnimation() {
        withContext(Dispatchers.Main) {
            loadingProgressBar.visibility = View.GONE
        }
    }
}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}