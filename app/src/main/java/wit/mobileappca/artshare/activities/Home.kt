package wit.mobileappca.artshare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.fragment_create.*
import org.wit.artshare.R
import org.wit.artshare.databinding.HomeBinding
import org.wit.artshare.helpers.readImage
import org.wit.artshare.models.ArtModel
import org.wit.artshare.models.Location
import wit.mobileappca.artshare.fragments.CreateFragment

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding : HomeBinding
    val imgRequest = 1
    val locationRequest = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = HomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        drawerLayout = homeBinding.drawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        val navView = homeBinding.navView
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
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