import PostsTranslations from "./Posts.translations.json";

import ShowAllPostsTranslations from "./screens/show-all-posts/ShowAllPosts.screen.translations.json";
import ShowPostDetailsTranslations from "./screens/show-post-details/ShowPostDetails.screen.translations.json";
import EditPostDetailsTranslations from "./screens/edit-post-details/EditPostDetails.screen.translations.json";
import AddNewPostTranslations from "./screens/add-new-post/AddNewPost.screen.translations.json";

export default {
  Posts: {
    ...PostsTranslations,
    Screen: {
      ...ShowAllPostsTranslations,
      ...ShowPostDetailsTranslations,
      ...EditPostDetailsTranslations,
      ...AddNewPostTranslations
    }
  }
};
