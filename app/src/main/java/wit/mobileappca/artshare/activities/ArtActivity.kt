package org.wit.artshare.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_art.*
import kotlinx.android.synthetic.main.activity_art_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.artshare.R
import org.wit.artshare.helpers.readImage
import org.wit.artshare.helpers.readImageFromPath
import org.wit.artshare.helpers.showImagePicker
import org.wit.artshare.main.MainApp
import org.wit.artshare.models.Location
import org.wit.artshare.models.ArtModel


class ArtActivity : AppCompatActivity(), AnkoLogger {

    var art = ArtModel()
    lateinit var app: MainApp
    var edit = false
    val imgRequest = 1
    val locationRequest = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art)
        app = application as MainApp

        //enable action bar and set title
        toolbarAdd.title = "Add Movie"
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("movie_edit")) {
            edit = true
            art = intent.extras?.getParcelable<ArtModel>("movie_edit")!!
            //set the title of the toolbar to the movie title
            toolbarAdd.title = art.title
            //populate the fields
            artTitle.setText(art.title)
            movieYear.setText(art.year.toString())
            movieDirector.setText(art.director)
            movieDescription.setText(art.description)
            rBar.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
                override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                    //if the rating is changed, update it in movie
                    art.rating = findViewById<RatingBar>(R.id.rBar).rating
                }
            })
            //populate the rating bar to previously set rating
            findViewById<RatingBar>(R.id.rBar).setRating(art.rating)
            //display image stored
            artImage.setImageBitmap(readImageFromPath(this, art.image))
            if (art.image != null) {
                chooseImage.setText(R.string.change_movie_image)
            }
            //update location button from 'add location' to 'change location'
            movieLocation.setText(R.string.change_button_location)
            //update 'add movie' text on button to 'save movie'
            btnAdd.setText(R.string.save_movie)
            //display the IMDB button
            IMDBBtn.isVisible = true;
        }

        movieLocation.setOnClickListener {
            /*set default location (DisneyWorld, Orlando) which user can keep if they're
            not interested in this feature*/
            val location = Location(28.385233, -81.563873, -20f)
            if (art.zoom != 0f) {
                location.lat = art.lat
                location.lng = art.lng
                location.zoom = art.zoom
            }
            //start Google Maps API
            startActivityForResult(
                    intentFor<GMapsActivity>().putExtra("location", location),
                    locationRequest
            )
        }

        infoBtn.setOnClickListener() {
            //Display info button message as toast notification
            toast(R.string.location_info)
        }

        btnAdd.setOnClickListener() {
            //parse the fields and assign them to their relevant values
            art.title = artTitle.text.toString()
            var year = movieYear.text.toString().toInt()
            art.director = movieDirector.text.toString()
            art.description = movieDescription.text.toString()
            art.rating = findViewById<RatingBar>(R.id.rBar).rating
            art.year = year
            //validation
            if (art.title.isEmpty()) {
                //title cannot be null - display error message
                toast(R.string.enter_movie_title)
            }
            //year must be between 1937 and 2021 (any other is impossible for a Disney movie)
            if (year < 1937 || year > 2021) {
                toast(R.string.enter_movie_year)
            }
            else {
                if (edit) {
                    //update the stored values
                    app.arts.update(art.copy())
                } else {
                    //create the movie
                    app.arts.create(art.copy())
                }
                info("Add Button Pressed: $artTitle")
                setResult(AppCompatActivity.RESULT_OK)
                //finish the activity
                finish()
                toast("Movie saved")
            }
        }

        chooseImage.setOnClickListener {
            //open image picker
            showImagePicker(this, imgRequest)
        }

        IMDBBtn.setOnClickListener {
            val imdb = Intent(
                //Sends the User to the IMDB Web page
                Intent.ACTION_VIEW,
                Uri.parse("https://www.imdb.com/find?q=" + art.title)
            )
            //starts the activity
            startActivity(imdb)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //display menu
        menuInflater.inflate(R.menu.menu_art, menu)
        //if the movie is in edit mode, display the delete and email buttons
        if (edit && menu != null) menu.getItem(0).setVisible(true)
        if (edit && menu != null) menu.getItem(1).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_email -> {
                val email = Intent(Intent.ACTION_SEND)
                //form email containing movie details
                email.putExtra(Intent.EXTRA_EMAIL, arrayOf("")) // recipients - will leave blank for users to complete
                email.putExtra(Intent.EXTRA_SUBJECT, "Check out this Disney Movie!")
                email.putExtra(Intent.EXTRA_TEXT, "Have a look at this movie: ${art.title}" +
                        if (art.year != null) {"\n Released in: ${art.year}"} else {} +
                        if (art.director != null) {"\n Directed by: ${art.director}"} else {} +
                        "\n \n I rated it " + art.rating + "/5.0 stars!")

                //need this to prompts email client only
                email.type = "message/rfc822"
                //starts the activity by choosing an email client, then going to send the email
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
            R.id.item_delete -> {
                val builder = AlertDialog.Builder(this@ArtActivity)
                builder.setMessage(("Are you sure you want to delete this movie?"))
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        // Delete item
                        app.arts.delete(art)
                        finish()
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                //display confirmation dialog
                alert.show()
            }
            R.id.item_cancel -> {
                //finish the activity
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            imgRequest -> {
                if (data != null) {
                    //parse image bitmap and set that as movie image attribute
                    art.image = data.getData().toString()
                    artImage.setImageBitmap(readImage(this, resultCode, data))
                    //Change text from add image to change image, as image has been added
                    chooseImage.setText(R.string.change_movie_image)
                }
            }
            locationRequest -> {
                if (data != null) {
                    //parse location of placemarker and make that the location with appropriate zoom
                    val location = data.extras?.getParcelable<Location>("location")!!
                    art.lat = location.lat
                    art.lng = location.lng
                    art.zoom = location.zoom
                }
            }
        }
    }
}