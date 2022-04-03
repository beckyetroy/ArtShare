package wit.mobileappca.artshare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import kotlinx.android.synthetic.main.fragment_create.*
import wit.mobileappca.artshare.R
import wit.mobileappca.artshare.databinding.HomeBinding
import wit.mobileappca.artshare.helpers.readImage
import wit.mobileappca.artshare.models.Location

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding : HomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    val imgRequest = 1
    val locationRequest = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = HomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        drawerLayout = homeBinding.drawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.
        findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.createFragment, R.id.listFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = homeBinding.navView
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            imgRequest -> {
                if (data != null) {
                    artImage.setImageBitmap(readImage(this,resultCode,data))
                    //Change text from add image to change image, as image has been added
                    chooseImage.setText(R.string.change_image)
                }
            }
            locationRequest -> {
                if (data != null) {
                    //parse location of art and make that the location with appropriate zoom
                    val location = data.extras?.getParcelable<Location>("location")!!
                    //art.lat = location.lat
                    //art.lng = location.lng
                    //art.zoom = location.zoom
                }
            }
        }
    }
}