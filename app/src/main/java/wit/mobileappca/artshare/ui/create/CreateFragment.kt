package wit.mobileappca.artshare.ui.create

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.fragment_create.*
import org.jetbrains.anko.toast
import timber.log.Timber
import wit.mobileappca.artshare.R
import wit.mobileappca.artshare.activities.GMapsActivity
import wit.mobileappca.artshare.databinding.FragmentCreateBinding
import wit.mobileappca.artshare.firebase.FirebaseImageManager
import wit.mobileappca.artshare.helpers.readImageUri
import wit.mobileappca.artshare.helpers.showImagePicker
import wit.mobileappca.artshare.main.MainApp
import wit.mobileappca.artshare.models.ArtModel
import wit.mobileappca.artshare.models.Location
import wit.mobileappca.artshare.ui.auth.LoggedInViewModel
import wit.mobileappca.artshare.ui.list.ListFragment
import wit.mobileappca.artshare.ui.list.ListViewModel
import java.util.Calendar.getInstance


class CreateFragment : Fragment() {

    private var _fragBinding: FragmentCreateBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var createViewModel: CreateViewModel
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>

    var art = ArtModel()
    var edit = false
    val locationRequest = 2
    var bm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        registerImagePickerCallback()
        //navController = Navigation.findNavController(activity!!, R.uid.nav_host_fragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentCreateBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.app_name)

        createViewModel =
            ViewModelProvider(this).get(CreateViewModel::class.java)
        createViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        val category: Spinner = fragBinding.artType
        // Create an ArrayAdapter using the artType array and a default spinner layout
        activity?.let {
            ArrayAdapter.createFromResource(
                it.baseContext, R.array.category_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the category
                category.adapter = adapter
            }
        }

        fragBinding.artLocation.setOnClickListener {
            /*set default location (Florence, Italy) which user can keep if they're
            not interested in this feature*/
            val location = Location(43.769562, 11.255814, -20f)
            if (art.zoom != 0f) {
                location.lat = art.lat
                location.lng = art.lng
                location.zoom = art.zoom
            }
            //start Google Maps API
            val i = Intent(activity, GMapsActivity::class.java)
            i.putExtra("location", location)
            startActivityForResult(i, locationRequest)
        }

        fragBinding.infoBtn.setOnClickListener() {
            //Display info button message as toast notification
            activity?.applicationContext?.toast(R.string.location_info)
        }

        setButtonListener(fragBinding)

        fragBinding.chooseImage.setOnClickListener {
            //open image picker
            showImagePicker(intentLauncher)
        }

        return root;
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.artError),Toast.LENGTH_LONG).show()
        }
    }

    fun setButtonListener(layout: FragmentCreateBinding) {
        layout.btnAdd.setOnClickListener() {
            //parse the fields and assign them to their relevant values
            var title = artTitle.text.toString()
            var image = bm
            var description = artDescription.text.toString()
            var type = artType.selectedItem.toString()

            var res: Resources = resources
            var types = res.getStringArray(R.array.category_array)

            var typeIndex = types.indexOf(art.type)

            var date = getInstance().time
            var email = loggedInViewModel.liveFirebaseUser.value?.email!!

            //validation
            if (title.isEmpty()) {
                //title cannot be null - display error message
                Toast.makeText(context, R.string.enter_art_title,Toast.LENGTH_LONG).show()
            }
            else if (type.isEmpty()) {
                //type cannot be null - display error message
                Toast.makeText(context, R.string.enter_art_type,Toast.LENGTH_LONG).show()
            }
            else if (image == null) {
                //image string cannot be empty - display error message
                Toast.makeText(context, R.string.enter_art,Toast.LENGTH_LONG).show()
            }
            else {
                //create the art piece
                createViewModel.addArt(loggedInViewModel.liveFirebaseUser,
                    ArtModel(title = title, image = image, description = description,
                    type = type, typeIndex = typeIndex, date = date, email = email))
                Toast.makeText(context, R.string.save_success,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerImagePickerCallback() {
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("registerPickerCallback() ${readImageUri(result.resultCode, result.data).toString()}")
                            FirebaseImageManager
                                .updateImage(loggedInViewModel.liveFirebaseUser.value!!.uid,
                                    loggedInViewModel.liveFirebaseUser.value!!.uid,
                                    readImageUri(result.resultCode, result.data),
                                    fragBinding.artImage,
                                    true)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create, menu)
        //if the art is in edit mode, display the menu with delete and email buttons
        if (edit && menu != null) menu.getItem(0).isVisible = true
        if (edit && menu != null) menu.getItem(1).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_email -> {
                val email = Intent(Intent.ACTION_SEND)
                //form email containing artwork details
                email.putExtra(Intent.EXTRA_EMAIL, arrayOf("")) // recipients - will leave blank for
                // users to complete
                email.putExtra(Intent.EXTRA_SUBJECT, "Check out this art I made!")
                email.putExtra(Intent.EXTRA_TEXT, "Have a look at this artwork: ${art.title}"
                        + if (art.description != null) {"\n ${art.description}"} else {} +
                        "\n Art Type: ${art.type}" + if (art.date != null) {". Made on ${art.date}."}
                else {"."})
                email.putExtra(Intent.EXTRA_STREAM, Uri.parse(art.image))

                //need this to prompts email client only
                email.type = "message/rfc822"
                //starts the activity by choosing an email client, then going to send the email
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        //TODO - Add total functionality here
        //val listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        //listViewModel.observableArtsList.observe(viewLifecycleOwner, Observer {
        //    totalArt = listViewModel.observableArtsList.value!!.sumOf { it.amount }
        //})
    }
}