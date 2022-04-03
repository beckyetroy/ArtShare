package wit.mobileappca.artshare.models

interface ArtStore {
    fun findAll(): List<ArtModel>
    fun create(art: ArtModel)
    fun search(searchTerm: String): List<ArtModel>
    fun update(art: ArtModel)
    fun delete(art: ArtModel)

}