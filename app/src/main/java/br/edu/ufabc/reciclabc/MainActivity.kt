package br.edu.ufabc.reciclabc

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.edu.ufabc.reciclabc.databinding.ActivityMainBinding
import br.edu.ufabc.reciclabc.ui.notifications.createaddressnotification.CreateAddressNotificationFragmentArgs
import br.edu.ufabc.reciclabc.ui.notifications.createnotification.CreateNotificationFragmentArgs
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_recycling_guide,
                R.id.navigation_collection_points,
                R.id.notifications_screen,
            ),
        )

        navController.addOnDestinationChangedListener { _, destination, args ->
            closeKeyboard()
            when (destination.id) {
                R.id.create_notification_screen -> {
                    navView.visibility = View.GONE
                    args?.apply {
                        if (CreateNotificationFragmentArgs.fromBundle(this).notification == null) {
                            destination.label =
                                getString(R.string.fragment_label_create_notification)
                        } else {
                            destination.label = getString(R.string.fragment_label_edit_notification)
                        }
                    }
                }
                R.id.create_address_notification_screen -> {
                    navView.visibility = View.GONE
                    args?.apply {
                        if (CreateAddressNotificationFragmentArgs.fromBundle(this).addressNotification == null) {
                            destination.label =
                                getString(R.string.fragment_label_create_notification)
                        } else {
                            destination.label = getString(R.string.fragment_label_edit_notification)
                        }
                    }
                }
                else -> navView.visibility = View.VISIBLE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
