package org.wit.artshare.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_art.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.artshare.R
import org.wit.artshare.helpers.readImage
import org.wit.artshare.helpers.readImageFromPath
import org.wit.artshare.helpers.showImagePicker
import org.wit.artshare.main.MainApp
import org.wit.artshare.models.ArtModel
import org.wit.artshare.models.Location
import java.time.LocalDate
import java.util.*
import java.util.Calendar.getInstance


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
        toolbarAdd.title = "Add Your Masterpiece"
        setSupportActionBar(toolbarAdd)

        //set Pinterest button to invisible and date picker to not render by default
        pinterestBtn.isInvisible = true
        artDate.isGone = true

        if (intent.hasExtra("art_edit")) {
            edit = true
            art = intent.extras?.getParcelable<ArtModel>("art_edit")!!
            //set the title of the toolbar to the artwork title
            toolbarAdd.title = art.title
            //populate the fields
            artTitle.setText(art.title)
            artDescription.setText(art.description)

            for (i in arrayListOf(R.array.category_array)) {
                if (artType.getItemAtPosition(i).toString() == art.type){
                    artType.setSelection(i)
                }
            }

            if (artDate.isVisible) {
                artDate.updateDate(getDateYear(art.date),getDateMonth(art.date),getDateDay(art.date))
            }

            //display image stored and update button text
            artImage.setImageBitmap(readImageFromPath(this, art.image))
            chooseImage.setText(R.string.change_image)

            //update location button from 'add location' to 'change location'
            artLocation.setText(R.string.change_button_location)
            //update 'add artwork' text on button to 'save artwork'
            btnAdd.setText(R.string.save_art)
        }

        val category: Spinner = findViewById(R.id.artType)
        // Create an ArrayAdapter using the artType array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the category
            category.adapter = adapter
        }

        /*Reveal the hidden datepicker when user selects to use it*/
        dateOption.setOnCheckedChangeListener{ _, isChecked ->
            artDate.isVisible = isChecked
        }

        artLocation.setOnClickListener {
            /*set default location (Florence, Italy) which user can keep if they're
            not interested in this feature*/
            val location = Location(43.769562, 11.255814, -20f)
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
            art.description = artDescription.text.toString()
            art.type = artType.selectedItem.toString()
            if (artDate.isVisible) {
                val datePicker = findViewById<View>(R.id.artDate) as DatePicker
                val day = datePicker.dayOfMonth
                val month = datePicker.month + 1
                val year = datePicker.year
                art.date = getDate(year,month,day)
            }

            //validation
            if (art.title.isEmpty()) {
                //title cannot be null - display error message
                toast(R.string.enter_art_title)
            }
            if (art.type.isEmpty()) {
                //type cannot be null - display error message
                toast(R.string.enter_art_type)
            }
            if (art.image.isEmpty()) {
                //image cannot be null - display error message
                toast(R.string.enter_art)
            }
            else {
                if (edit) {
                    //update the stored values
                    app.arts.update(art.copy())
                } else {
                    //create the art piece
                    app.arts.create(art.copy())
                }
                info("Add Button Pressed: $artTitle")
                setResult(AppCompatActivity.RESULT_OK)
                //finish the activity
                finish()
                toast("Artwork saved")
            }
        }

        chooseImage.setOnClickListener {
            //open image picker
            showImagePicker(this, imgRequest)
        }

        pinterestBtn.setOnClickListener {
            val pinterest = Intent(
                //Sends the User to Pinterest to compare similarly titled works
                Intent.ACTION_VIEW,
                Uri.parse("https://www.pinterest.ie/search/pins/?q=" + art.title)
            )
            //starts the activity
            startActivity(pinterest)
        }
    }

    /*Creates extension function so getdate() returns date object given year month and day*/
    fun getDate(year: Int, month: Int, day: Int): Date {
        var cal = getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month
        cal[Calendar.DAY_OF_MONTH] = day
        return cal.time
    }

    /*Creates extension functions to return year, month, and day given date object*/
    fun getDateYear(date: Date): Int {
        var cal = getInstance()
        cal.time = date
        var year = cal[Calendar.YEAR]
        return year
    }

    fun getDateMonth(date: Date): Int {
        var cal = getInstance()
        cal.time = date
        var month = cal[Calendar.MONTH]
        return month
    }

    fun getDateDay(date: Date): Int {
        var cal = getInstance()
        cal.time = date
        var day = cal[Calendar.DAY_OF_MONTH]
        return day
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //display menu
        menuInflater.inflate(R.menu.menu_art, menu)
        //if the art is in edit mode, display the delete and email buttons
        if (edit && menu != null) menu.getItem(0).isVisible = true
        if (edit && menu != null) menu.getItem(1).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_email -> {
                val email = Intent(Intent.ACTION_SEND)
                //form email containing artwork details
                email.putExtra(Intent.EXTRA_EMAIL, arrayOf("")) // recipients - will leave blank for users to complete
                email.putExtra(Intent.EXTRA_SUBJECT, "Check out this art I made!")
                email.putExtra(Intent.EXTRA_TEXT, "Have a look at this artwork: ${art.title}" +
                        if (art.description != null) {"\n ${art.description}"} else {} +
                        "\n Made with: ${art.type}" + if (art.date != null) {"on ${art.date}."} else {"."})
                email.putExtra(Intent.EXTRA_STREAM, art.image)

                //need this to prompts email client only
                email.type = "message/rfc822"
                //starts the activity by choosing an email client, then going to send the email
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
            R.id.item_delete -> {
                val builder = AlertDialog.Builder(this@ArtActivity)
                builder.setMessage(("Are you sure you want to delete this artwork?"))
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
                    //parse image bitmap and set that as art image attribute
                    art.image = data.getData().toString()
                    artImage.setImageBitmap(readImage(this, resultCode, data))
                    //Change text from add image to change image, as image has been added
                    chooseImage.setText(R.string.change_image)
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