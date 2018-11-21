import { DigitLayout, DigitTranslations } from "@cthit/react-digit-components";
import React from "react";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import PostForm from "../common-views/post-form";
import translations from "./EditPostDetails.screen.translations.json";

const EditPostDetails = ({ postsChange, post, match }) => (
    <IfElseRendering
        test={post != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Posts.Screen.EditPostDetails"
                render={text => (
                    <DigitLayout.Center>
                        <PostForm
                            onSubmit={(values, actions) => {
                                postsChange(values, match.params.id);
                            }}
                            initialValues={{
                                sv: post.sv == null ? "" : post.sv,
                                en: post.en == null ? "" : post.en
                            }}
                            titleText={text.UpdatePost}
                            swedishInputText={text.SwedishInput}
                            englishInputText={text.EnglishInput}
                            submitText={text.SavePost}
                            fieldRequiredText={text.fieldRequiredText}
                        />
                    </DigitLayout.Center>
                )}
            />
        )}
    />
);

export default EditPostDetails;
