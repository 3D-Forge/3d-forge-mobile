package com.example.a3dforge.activities

import OkHttpConfig
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.adapter.ProductAdapter
import com.example.a3dforge.factories.CatalogSearchViewModelFactory
import com.example.a3dforge.models.CatalogSearchViewModel
import com.example.a3dforge.models.SharedViewModel


class CatalogFragment : Fragment() {

    private lateinit var rcView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var filterButton: Button
    private lateinit var adapter: ProductAdapter
    private lateinit var sortSpinner: Spinner

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        println(sharedViewModel.filterParameters)
        println(sharedViewModel.filterParameters)
        println(sharedViewModel.filterParameters)
        println(sharedViewModel.filterParameters)
        println(sharedViewModel.filterParameters)
        println(sharedViewModel.filterParameters)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        filterButton = view.findViewById(R.id.filterButton)
        rcView = view.findViewById(R.id.rcView)

        sortSpinner = view.findViewById(R.id.sortSpinner)
        val sortItems = listOf("Спочатку популярні", "Спочатку Дешеві", "Спочатку Дорогі", "За Оцінкою")
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, sortItems)
        sortSpinner.adapter = arrayAdapter

        adapter = ProductAdapter(requireActivity())
        val spanCount = 2
        rcView.layoutManager = GridLayoutManager(this.requireContext(), spanCount)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        rcView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacingInPixels, true))

        rcView.adapter = adapter

        val okHttpConfig = OkHttpConfig

        val catalogSearchViewModel = ViewModelProvider(this, CatalogSearchViewModelFactory(okHttpConfig))
            .get(CatalogSearchViewModel::class.java)

        catalogSearchViewModel.profileResult.observe(viewLifecycleOwner, Observer { result  ->
            result.second?.let {
                val list = result.second!!.data.items
                adapter.submitList(list)
            }
        })

        catalogSearchViewModel.getCatalog(
            sharedViewModel.filterParameters.q,
            sharedViewModel.filterParameters.categories,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )

        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchButton.setOnClickListener {
                    sharedViewModel.filterParameters.q = searchEditText.text.toString()
                    catalogSearchViewModel.getCatalog(
                        sharedViewModel.filterParameters.q,
                        sharedViewModel.filterParameters.categories,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                }
                true
            } else {
                false
            }
        }

        searchButton.setOnClickListener {
            sharedViewModel.filterParameters.q = searchEditText.text.toString()
            catalogSearchViewModel.getCatalog(
                sharedViewModel.filterParameters.q,
                sharedViewModel.filterParameters.categories,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        }


        filterButton.setOnClickListener{
            val searchfilterFragment = SearchFilterFragment()

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, searchfilterFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
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