package wit.mobileappca.artshare.ui.detail

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import timber.log.Timber
import wit.mobileappca.artshare.R
import wit.mobileappca.artshare.databinding.FragmentArtDetailBinding
import wit.mobileappca.artshare.helpers.readImageFromPath
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentArtDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        val imgRequest = 1
        var bm = ""

        detailViewModel = ViewModelProvider(this).get(ArtDetailViewModel::class.java)
        detailViewModel.observableArt.observe(viewLifecycleOwner, Observer { render() })

        //populate the fields
        fragBinding.artTitle.setText(detailViewModel.observableArt.value?.title)
        fragBinding.artDescription.setText(detailViewModel.observableArt.value?.description)

        var res: Resources = resources
        var types = res.getStringArray(R.array.category_array)

        var index = types.indexOf(detailViewModel.observableArt.value?.type)
        fragBinding.artType.setSelection(index)

        //show the date and date title with value
        fragBinding.date.text = detailViewModel.observableArt.value?.date.toString()

        //display image stored and update button text
        fragBinding.artImage.setImageBitmap(detailViewModel.observableArt.value?.image?.let {
            readImageFromPath(fragBinding.artImage.context,
                it
            )
        })

        fragBinding.chooseImage.setOnClickListener {
            //open image picker
            activity?.let { it1 -> showImagePicker(it1, imgRequest) }
            if ((fragBinding.artImage.drawable as BitmapDrawable).bitmap != null) {
                bm = (fragBinding.artImage.drawable as BitmapDrawable).bitmap.toString()
                bm = detailViewModel.observableArt.value?.image!!
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

        fragBinding.btnEdit.setOnClickListener {
            detailViewModel.updateArt(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                args.artid, fragBinding.artvm?.observableArt!!.value!!)
            //Force Reload of list to guarantee refresh
            listViewModel.load()
            findNavController().navigateUp()
            findNavController().popBackStack()

        }

        fragBinding.btnDelete.setOnClickListener {
            listViewModel.delete(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                detailViewModel.observableArt.value?.id!!)
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
}