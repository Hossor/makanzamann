package ir.nahad.makanzaman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cardview.view.*

class worksheetAdapter (private val worksheetlist:List<worksheetItems>):
    RecyclerView.Adapter<worksheetAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): worksheetAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview , parent , false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: worksheetAdapter.ViewHolder, position: Int) {
        val item = worksheetlist[position]
        holder.Start.text = item.start
        holder.End.text = item.end
        holder.Date.text = item.date
        holder.worksheet.text = item.karkerd
    }

    override fun getItemCount(): Int {
        return worksheetlist.size
    }
    class ViewHolder(ItemView:View):RecyclerView.ViewHolder(ItemView){
        val Start: TextView = itemView.startcardView
        val End :TextView = itemView.endcardview
        val Date :TextView = itemView.datecardView
        val worksheet:TextView = itemView.worksheetcardview

    }
}