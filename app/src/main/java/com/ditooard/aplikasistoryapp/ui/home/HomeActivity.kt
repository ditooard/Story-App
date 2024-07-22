package com.ditooard.aplikasistoryapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditooard.aplikasistoryapp.R
import com.ditooard.aplikasistoryapp.adapter.StoryListAdapter
import com.ditooard.aplikasistoryapp.databinding.ActivityHomeBinding
import com.ditooard.aplikasistoryapp.ui.main.MainActivity
import com.ditooard.aplikasistoryapp.ui.main.UserViewModel
import com.ditooard.aplikasistoryapp.ui.main.UserPreferences
import com.ditooard.aplikasistoryapp.ui.main.dataStore
import com.ditooard.aplikasistoryapp.ui.register.DetailStory
import com.ditooard.aplikasistoryapp.ui.story.TambahStoryActivity
import com.ditooard.aplikasistoryapp.viewmodel.CustomViewModelFactory

class HomeActivity : AppCompatActivity() {
    private val preferences by lazy { UserPreferences.getInstance(dataStore) }
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userToken: String
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        initializeUserPreferences()
        setupObservers()
        setupClickListeners()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.home)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        binding.rvStories.addItemDecoration(
            DividerItemDecoration(this, layoutManager.orientation)
        )
    }

    private fun initializeUserPreferences() {
        val userLoginViewModel = ViewModelProvider(
            this, CustomViewModelFactory(preferences)
        )[UserViewModel::class.java]

        userLoginViewModel.getToken().observe(this) {
            userToken = it
            homeViewModel.fetchStories(userToken)
        }
    }

    private fun setupObservers() {
        homeViewModel.message.observe(this) { message ->
            displayStories(homeViewModel.stories.value ?: emptyList())
            displayToast(message)
        }

        homeViewModel.isLoading.observe(this) { isLoading ->
            toggleLoadingIndicator(isLoading)
        }
    }


    private fun displayStories(stories: List<DetailStory>) {
        toggleNoDataMessage(stories.isEmpty())

        val adapter = StoryListAdapter(stories)
        binding.rvStories.adapter = adapter

        adapter.setOnStoryClickCallback(object : StoryListAdapter.OnStoryClickCallback {
            override fun onStoryClicked(story: DetailStory) {
                navigateToDetailActivity(story)
            }
        })
    }



    private fun toggleNoDataMessage(isEmpty: Boolean) {
        binding.noDataFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.tvNoDataFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun navigateToDetailActivity(story: DetailStory) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.STORY, story)
        }
        startActivity(intent)
    }

    private fun setupClickListeners() {
        binding.btnFloating.setOnClickListener {
            startActivity(Intent(this, TambahStoryActivity::class.java))
        }

        binding.pullRefresh.setOnRefreshListener {
            homeViewModel.fetchStories(userToken)
            binding.pullRefresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refreshApp -> {
                homeViewModel.fetchStories(userToken)
                true
            }
            R.id.changeLanguage -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.logout))
            setMessage(getString(R.string.logoutDescription))
            setPositiveButton(getString(R.string.cancelLogout)) { dialog, _ ->
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.logoutButton)) { _, _ ->
                performLogout()
            }
            create().show()
        }
    }

    private fun performLogout() {
        val loginViewModel = ViewModelProvider(
            this, CustomViewModelFactory(preferences)
        )[UserViewModel::class.java]

        loginViewModel.clearLoginData()
        Toast.makeText(this, R.string.SuccessLogout, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun displayToast(message: String) {
        if (homeViewModel.isError.value == true) {
            Toast.makeText(
                this, getString(R.string.error_load) + " $message", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun toggleLoadingIndicator(isVisible: Boolean) {
        binding.progressBar3.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
