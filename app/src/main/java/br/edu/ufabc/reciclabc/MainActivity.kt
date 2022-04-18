package br.edu.ufabc.reciclabc

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.edu.ufabc.reciclabc.databinding.ActivityMainBinding
import br.edu.ufabc.reciclabc.ui.notifications.CreateAddressNotificationFragmentArgs
import br.edu.ufabc.reciclabc.ui.notifications.CreateNotificationFragmentArgs
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_recycling_guide,
                R.id.navigation_collection_points,
                R.id.navigation_notifications
            )
        )

        navController.addOnDestinationChangedListener { _, destination, args ->
            when (destination.id) {
                R.id.create_notification_fragment -> {
                    navView.visibility = View.GONE
                    args?.apply {
                        if (CreateNotificationFragmentArgs.fromBundle(this).notification == null) {
                            destination.label = getString(R.string.fragment_label_create_notification)
                        } else {
                            destination.label = getString(R.string.fragment_label_edit_notification)
                        }
                    }
                }
                R.id.create_address_notification_fragment -> {
                    navView.visibility = View.GONE
                    args?.apply {
                        if (CreateAddressNotificationFragmentArgs.fromBundle(this).addressNotification == null) {
                            destination.label = getString(R.string.fragment_label_create_notification)
                        } else {
                            destination.label = getString(R.string.fragment_label_edit_notification)
                        }
                    }
                }
                else -> navView.visibility = View.VISIBLE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}