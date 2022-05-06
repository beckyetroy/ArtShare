package wit.mobileappca.artshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_art.view.*
import wit.mobileappca.artshare.databinding.CardArtBinding
import wit.mobileappca.artshare.helpers.readImageFromPath
import wit.mobileappca.artshare.models.ArtModel
import java.util.*


interface ArtListener {
    fun onArtClick(art: ArtModel)
}

class ArtAdapter constructor(private var arts: MutableList<ArtModel>,
                             private val listener: ArtListener
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

        return MainHolder(binding)
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

    class MainHolder(val binding : CardArtBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(art: ArtModel, listener : ArtListener) {
            //populates the art cards with title, description, and image
            binding.art = art
            binding.imageIcon.setImageBitmap(readImageFromPath(itemView.context, art.image))
            binding.artTitle.text = art.title
            binding.artType.text = art.type
            //start method onArtClick for that particular art
            binding.root.setOnClickListener { listener.onArtClick(art) }
            binding.executePendingBindings()
            //itemView.artTitle.text = art.title
            //itemView.artType.text = art.type
            //itemView.imageIcon.setImageBitmap(readImageFromPath(itemView.context, art.image))
            //start method onArtClick for that particular art
            //itemView.setOnClickListener { listener.onArtClick(art) }
        }
    }
}