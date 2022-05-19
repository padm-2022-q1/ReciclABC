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
import br.edu.ufabc.reciclabc.ui.notifications.details.AddressDetailsFragmentArgs
import br.edu.ufabc.reciclabc.ui.notifications.details.NotificationDetailsFragmentArgs
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ReminderReceiver.createNotificationChannel(this)

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
                R.id.create_notification_group_screen -> {
                    navView.visibility = View.GONE
                    args?.apply {
                        if (NotificationDetailsFragmentArgs.fromBundle(this).notificationGroupId > 0) {
                            destination.label = getString(R.string.fragment_label_edit_notification)
                        } else {
                            destination.label =
                                getString(R.string.fragment_label_create_notification)
                        }
                    }
                }
                R.id.address_details_screen -> {
                    navView.visibility = View.GONE
                    args?.apply {
                        if (AddressDetailsFragmentArgs.fromBundle(this).addressId > 0) {
                            destination.label = getString(R.string.notifications_edit_address)
                        } else {
                            destination.label = getString(R.string.notifications_add_address)
                        }
                    }
                }
                else -> navView.visibility = View.VISIBLE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
