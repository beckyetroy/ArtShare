package wit.mobileappca.artshare.ui.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_art_detail.*
import timber.log.Timber
import wit.mobileappca.artshare.R
import wit.mobileappca.artshare.databinding.FragmentArtDetailBinding
import wit.mobileappca.artshare.firebase.FirebaseImageManager
import wit.mobileappca.artshare.helpers.readImageUri
import wit.mobileappca.artshare.helpers.showImagePicker
import wit.mobileappca.artshare.ui.auth.LoggedInViewModel
import wit.mobileappca.artshare.ui.list.ListViewModel

class ArtDetailFragment : Fragment() {
    private lateinit var detailViewModel: ArtDetailViewModel
    private val args by navArgs<ArtDetailFragmentArgs>()
    private var _fragBinding: FragmentArtDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val listViewModel : ListViewModel by activityViewModels()
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentArtDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        registerImagePickerCallback()

        detailViewModel = ViewModelProvider(this).get(ArtDetailViewModel::class.java)
        detailViewModel.observableArt.observe(viewLifecycleOwner, Observer { render() })

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

        fragBinding.pinterestBtn.setOnClickListener {
            val pinterest = Intent(
                //Sends the User to Pinterest to compare similarly titled works
                Intent.ACTION_VIEW,
                Uri.parse("https://www.pinterest.ie/search/pins/?q=" +
                        detailViewModel.observableArt.value?.title + "%20" +
                        detailViewModel.observableArt.value?.type)
            )
            //starts the activity
            startActivity(pinterest)
        }

        fragBinding.chooseImage.setOnClickListener {
            //open image picker
            showImagePicker(intentLauncher)
        }

        fragBinding.btnEdit.setOnClickListener {
            detailViewModel.updateArt(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                args.artid, fragBinding.artvm?.observableArt!!.value!!)
            //Force Reload of list to guarantee refresh
            listViewModel.load()
            findNavController().navigateUp()
        }

        fragBinding.btnDelete.setOnClickListener {
            listViewModel.delete(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                detailViewModel.observableArt.value?.uid!!)
            findNavController().navigateUp()
        }
        return root
    }

    private fun render() {
        fragBinding.artvm = detailViewModel
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getArt(loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.artid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    private fun registerImagePickerCallback() {
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode){
                    Activity.RESULT_OK -> {
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
                    Activity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}