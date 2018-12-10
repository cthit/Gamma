import React from "react";
import { Route, Switch } from "react-router-dom";

import ShowAllPosts from "./screens/show-all-posts";
import AddNewPost from "./screens/add-new-post";
import ShowPostDetails from "./screens/show-post-details";
import EditPostDetails from "./screens/edit-post-details";

const Posts = () => (
    <Switch>
        <Route path="/posts" exact component={ShowAllPosts} />
        <Route path="/posts/add" exact component={AddNewPost} />
        <Route path="/posts/:id" exact component={ShowPostDetails} />
        <Route path="/posts/:id/edit" exact component={EditPostDetails} />
    </Switch>
);

export default Posts;
