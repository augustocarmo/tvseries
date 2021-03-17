package carmo.augusto.tvseries.ui.activities

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import carmo.augusto.core.extensions.exhaustive
import carmo.augusto.tvseries.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val navController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment

        navHostFragment.navController
    }
    private val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(
            R.id.homeFragment
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    setActionBarTitle(R.string.page_home)
                }
                R.id.seriesFragment -> {
                    setActionBarTitle(R.string.page_series)
                }
                R.id.episodeFragment -> {
                    setActionBarTitle(R.string.page_episode)
                }
                R.id.searchSeriesFragment -> {
                    setActionBarTitle(R.string.page_search_series)
                }
                else -> Unit
            }.exhaustive
        }
    }

    private fun setActionBarTitle(@StringRes titleResId: Int) {
        supportActionBar?.title = getString(titleResId)
    }
}