package org.wit.artshare.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_art.view.*
import kotlinx.android.synthetic.main.card_art.view.artTitle
import org.wit.artshare.R
import org.wit.artshare.helpers.readImageFromPath
import org.wit.artshare.models.ArtModel
import java.util.*
import android.widget.Filter
import android.widget.Filterable
import kotlin.collections.ArrayList

interface ArtListener {
    fun onArtClick(art: ArtModel)
}

class ArtAdapter constructor(private var arts: List<ArtModel>,
                             private val listener: ArtListener) : RecyclerView.Adapter<ArtAdapter.MainHolder>(), Filterable {

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
                        if (row.title.toLowerCase(Locale.ROOT)
                                        .contains(charSearch.toLowerCase(Locale.ROOT))
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
        return MainHolder(LayoutInflater.from(parent?.context).inflate(R.layout.card_art, parent, false))
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val art = arts[holder.adapterPosition]
        holder.bind(art, listener)
    }

    override fun getItemCount(): Int = arts.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(art: ArtModel, listener : ArtListener) {
            //populates the art cards with title, description, and image
            itemView.artTitle.text = art.title
            itemView.description.text = art.description
            itemView.imageIcon.setImageBitmap(readImageFromPath(itemView.context, art.image))
            //start method onArtClick for that particular art
            itemView.setOnClickListener { listener.onArtClick(art) }
        }
    }
}