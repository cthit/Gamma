import {
    DigitLayout,
    DigitTranslations,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import PostForm from "../common-views/post-form";
import translations from "./EditPostDetails.screen.translations.json";

class EditPostDetails extends Component {
    componentDidMount() {
        const { getPost, postId, gammaLoadingFinished } = this.props;

        getPost(postId).then(() => {
            gammaLoadingFinished();
        });
    }

    render() {
        const { editPost, post, postId } = this.props;
        return (
            <DigitIfElseRendering
                test={post != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        uniquePath="Posts.Screen.EditPostDetails"
                        render={text => (
                            <DigitLayout.Center>
                                <PostForm
                                    onSubmit={(values, actions) => {
                                        editPost(values, postId);
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
    }
}

export default EditPostDetails;
