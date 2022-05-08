package wit.mobileappca.artshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import wit.mobileappca.artshare.databinding.CardArtBinding
import wit.mobileappca.artshare.helpers.customTransformation
import wit.mobileappca.artshare.models.ArtModel
import java.util.*


interface ArtClickListener {
    fun onArtClick(art: ArtModel)
}

class ArtAdapter constructor(private var arts: MutableList<ArtModel>,
                             private val listener: ArtClickListener,
                             private val readOnly: Boolean
) : RecyclerView.Adapter<ArtAdapter.MainHolder>(), Filterable {

    var artFilterList = ArrayList<ArtModel>()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    //display all arts with no conditions
                    for (i in arts.indices) {
                        artFilterList.add(arts[i])
                    }
                } else {
                    val resultList = ArrayList<ArtModel>()
                    /*case sensitive - if artwork title matches query searched add it to the
                     resultlist*/
                    for (row in arts) {
                        if (row.title.lowercase(Locale.ROOT)
                                        .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    artFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = artFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                //display artFilterList
                artFilterList = results?.values as ArrayList<ArtModel>
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        //Display arts as cards
        val binding = CardArtBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding, readOnly)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val art = arts[holder.adapterPosition]
        holder.bind(art, listener)
    }

    fun removeAt(position: Int) {
        arts.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = arts.size

    class MainHolder(val binding : CardArtBinding, private val readOnly : Boolean) : RecyclerView.ViewHolder(binding.root) {
        val readOnlyRow = readOnly

        fun bind(art: ArtModel, listener : ArtClickListener) {
            binding.root.tag = art
            binding.art = art

            Picasso.get().load(art.image.toUri())
                .resize(200, 200)
                .transform(customTransformation())
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(binding.image)

            binding.root.setOnClickListener { listener.onArtClick(art) }
            binding.executePendingBindings()
        }
    }
}