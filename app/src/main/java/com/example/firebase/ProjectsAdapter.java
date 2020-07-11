package com.example.firebase;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder> {

    Context context;
    ArrayList<ProjectData> data;

    public ProjectsAdapter(Context context, ArrayList<ProjectData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.project_item,parent,false);
        return new ProjectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsViewHolder holder, int position) {
        ProjectData projectData=data.get(position);
        Picasso.get().load(Uri.parse(projectData.getImageUrl())).into(holder.projectImage);
        holder.projectTitle.setText(projectData.getTitle());
        holder.projectDesc.setText(projectData.getDescription());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{

        ImageView projectImage;
        TextView projectTitle, projectDesc;
        public ProjectsViewHolder(@NonNull View itemView) {
            super(itemView);

            projectImage=itemView.findViewById(R.id.project_image_view);
            projectTitle=itemView.findViewById(R.id.project_title_text_view);
            projectDesc=itemView.findViewById(R.id.project_description_text_view);
        }
    }
}
