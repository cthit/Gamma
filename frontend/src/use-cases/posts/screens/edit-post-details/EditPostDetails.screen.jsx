import React from "react";
import * as yup from "yup";
import GammaForm from "../../../../common/elements/gamma-form";
import GammaFormField from "../../../../common/elements/gamma-form-field";
import {
    GammaCard,
    GammaCardTitle,
    GammaCardBody,
    GammaCardButtons
} from "../../../../common-ui/design";
import GammaTextField from "../../../../common/elements/gamma-text-field";
import GammaButton from "../../../../common/elements/gamma-button";
import { Center } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import PostForm from "../common-views/post-form";
import { DigitTranslations } from "@cthit/react-digit-components";
import translations from "./EditPostDetails.screen.translations.json";

const EditPostDetails = ({ postsChange, post, match }) => (
    <IfElseRendering
        test={post != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Posts.Screen.EditPostDetails"
                render={text => (
                    <Center>
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
                    </Center>
                )}
            />
        )}
    />
);

export default EditPostDetails;
