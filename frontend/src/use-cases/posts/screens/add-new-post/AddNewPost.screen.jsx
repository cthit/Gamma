import { DigitLayout, DigitTranslations } from "@cthit/react-digit-components";
import React, { Component } from "react";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import PostForm from "../common-views/post-form";
import translations from "./AddNewPost.screen.translations.json";
import * as PropTypes from "prop-types";

class AddNewPost extends Component {
    constructor(props) {
        super(props);
        props.gammaLoadingFinished();
    }

    render() {
        const { addPost, toastOpen } = this.props;
        return (
            <DigitTranslations
                translations={translations}
                render={text => (
                    <DigitLayout.Center>
                        <PostForm
                            onSubmit={(values, actions) => {
                                addPost(values)
                                    .then(() => {
                                        toastOpen({ text: text.PostAdded });
                                        actions.setSubmitting(false);
                                        actions.resetForm();
                                    })
                                    .catch(error => {
                                        const code = statusCode(error);
                                        const message = statusMessage(error);
                                        switch (code) {
                                            case 422:
                                                switch (message) {
                                                    case "POST_ALREADY_EXISTS":
                                                        toastOpen({
                                                            text:
                                                                text.PostAlreadyExists,
                                                            duration: 6000
                                                        });
                                                        actions.setErrors({
                                                            sv: "",
                                                            en: ""
                                                        });
                                                        break;
                                                    default:
                                                        toastOpen({
                                                            text:
                                                                text.SomethingWentWrong,
                                                            duration: 6000
                                                        });
                                                }
                                                break;
                                            default:
                                                toastOpen({
                                                    text:
                                                        text.SomethingWentWrong,
                                                    duration: 10000
                                                });
                                        }
                                    });
                            }}
                            initialValues={{ sv: "", en: "" }}
                            titleText={text.AddNewPost}
                            swedishInputText={text.SwedishInput}
                            englishInputText={text.EnglishInput}
                            submitText={text.CreatePost}
                            fieldRequiredText={text.FieldRequired}
                        />
                    </DigitLayout.Center>
                )}
            />
        );
    }
}

export default AddNewPost;
