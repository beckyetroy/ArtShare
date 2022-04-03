package wit.mobileappca.artshare.ui.create

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_create.*
import org.jetbrains.anko.toast
import wit.mobileappca.artshare.R
import wit.mobileappca.artshare.activities.GMapsActivity
import wit.mobileappca.artshare.databinding.FragmentCreateBinding
import wit.mobileappca.artshare.helpers.readImageFromPath
import wit.mobileappca.artshare.helpers.showImagePicker
import wit.mobileappca.artshare.main.MainApp
import wit.mobileappca.artshare.models.ArtModel
import wit.mobileappca.artshare.models.Location
import wit.mobileappca.artshare.ui.list.ListFragment
import wit.mobileappca.artshare.ui.list.ListViewModel
import java.util.Calendar.getInstance


class CreateFragment : Fragment() {

    lateinit var app: MainApp
    private var _fragBinding: FragmentCreateBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var createViewModel: CreateViewModel

    var art = ArtModel()
    var edit = false
    val imgRequest = 1
    val locationRequest = 2
    var bm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
        //navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
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
        //val textView: TextView = root.findViewById(R.id.text_home)
        createViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        //set Pinterest button to invisible and date to not render by default
        fragBinding.pinterestBtn.isInvisible = true
        fragBinding.date.isGone = true
        fragBinding.dateTitle.isGone = true

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

        if (arguments?.isEmpty == false) {
            edit = true
            art = requireArguments().getParcelable<ArtModel>("art_edit")!!
            //populate the fields
            fragBinding.artTitle.setText(art.title)
            fragBinding.artDescription.setText(art.description)

            var res: Resources = resources
            var types = res.getStringArray(R.array.category_array)

            var index = types.indexOf(art.type)
            fragBinding.artType.setSelection(index)

            //show the date and date title with value
            fragBinding.dateTitle.isGone = false
            fragBinding.date.isGone = false
            fragBinding.date.text = art.date.toString()

            //display image stored and update button text
            fragBinding.artImage.setImageBitmap(readImageFromPath(fragBinding.artImage.context, art.image))
            fragBinding.chooseImage.setText(R.string.change_image)

            //update location button from 'add location' to 'change location'
            fragBinding.artLocation.setText(R.string.change_button_location)
            //update 'add artwork' text on button to 'save artwork'
            fragBinding.btnAdd.setText(R.string.save_art)
            //display the Pinterest Button
            fragBinding.pinterestBtn.isInvisible = false
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

        fragBinding.btnAdd.setOnClickListener() {
            //parse the fields and assign them to their relevant values
            art.title = artTitle.text.toString()
            art.image = bm
            art.description = artDescription.text.toString()
            art.type = artType.selectedItem.toString()
            art.date = getInstance().time

            //validation
            if (art.title.isEmpty()) {
                //title cannot be null - display error message
                Toast.makeText(context, R.string.enter_art_title,Toast.LENGTH_LONG).show()
            }
            else if (art.type.isEmpty()) {
                //type cannot be null - display error message
                Toast.makeText(context, R.string.enter_art_type,Toast.LENGTH_LONG).show()
            }
            else if (art.image == null) {
                //image string cannot be empty - display error message
                Toast.makeText(context, R.string.enter_art,Toast.LENGTH_LONG).show()
            }
            else {
                if (edit) {
                    //update the stored values
                    createViewModel.updateArt(art)
                } else {
                    //create the art piece
                    createViewModel.addArt(art)
                }
                Toast.makeText(context, R.string.save_success,Toast.LENGTH_LONG).show()
                //go back to view fragment
                val fm: FragmentManager? = fragmentManager
                if (fm != null) {
                    if (fm.backStackEntryCount > 0) {
                        fm.popBackStack()
                    }
                }
                if (!edit) {
                    activity?.recreate()
                }
                else {
                    val listFragment = ListFragment()
                    fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.nav_host_fragment, listFragment)
                        ?.commit()
                }
            }
        }

        fragBinding.chooseImage.setOnClickListener {
            //open image picker
            activity?.let { it1 -> showImagePicker(it1, imgRequest) }
            if ((fragBinding.artImage.drawable as BitmapDrawable).bitmap != null) {
                bm = (fragBinding.artImage.drawable as BitmapDrawable).bitmap.toString()
            }
        }

        fragBinding.pinterestBtn.setOnClickListener {
            val pinterest = Intent(
                //Sends the User to Pinterest to compare similarly titled works
                Intent.ACTION_VIEW,
                Uri.parse("https://www.pinterest.ie/search/pins/?q=" + art.title + "%20" +
                        art.type)
            )
            //starts the activity
            startActivity(pinterest)
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
            R.id.item_delete -> {
                val builder = activity?.let { AlertDialog.Builder(it) }
                builder?.setMessage(("Are you sure you want to delete this artwork?"))
                    ?.setCancelable(false)?.setPositiveButton("Yes") { dialog, id ->
                        // Delete item
                        createViewModel.deleteArt(art)
                        val listFragment = ListFragment()
                        fragmentManager
                            ?.beginTransaction()
                            ?.add(R.id.nav_host_fragment, listFragment)
                            ?.commit()
                }?.setNegativeButton("Cancel") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
                //display confirmation dialog
                builder?.create()?.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateFragment().apply {
                arguments = Bundle().apply {}
            }
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
//    }

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