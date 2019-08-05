import React, { useEffect } from "react";

import {
    DigitCRUD,
    DigitText,
    useDigitTranslations,
    DigitTextField
} from "@cthit/react-digit-components";

import translations from "./Posts.translations";
import { getPost, getPosts, getPostUsage } from "../../api/posts/get.posts.api";
import DisplayPostUsages from "./elements/display-post-usages";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { addPost } from "../../api/posts/post.posts.api";
import { deletePost } from "../../api/posts/delete.posts.api";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../api/utils/commonProps";

import * as yup from "yup";
import { editPost } from "../../api/posts/put.posts.api";

function generateValidationSchema(text) {
    const schema = {};

    schema[SWEDISH_LANGUAGE] = yup.string().required(text.FieldRequired);
    schema[ENGLISH_LANGUAGE] = yup.string().required(text.FieldRequired);

    return yup.object().shape(schema);
}

function generateEditComponentData(text) {
    const componentData = {};

    componentData[SWEDISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.SwedishInput
        }
    };

    componentData[ENGLISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.EnglishInput
        }
    };

    return componentData;
}

const Posts = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    return (
        <DigitCRUD
            path={"/posts"}
            name={"posts"}
            updateRequest={(id, data) =>
                editPost(id, { post: { sv: data.sv, en: data.en } })
            }
            createRequest={data =>
                addPost({ post: { sv: data.sv, en: data.en } })
            }
            readAllRequest={getPosts}
            readOneRequest={id => Promise.all([getPost(id), getPostUsage(id)])}
            createTitle={text.AddNewPost}
            keysText={{
                id: "Id",
                sv: text.Swedish,
                en: text.English
            }}
            keysOrder={["id", "sv", "en"]}
            tableProps={{
                titleText: text.Posts,
                search: true
            }}
            detailsButtonText={text.Details}
            deleteRequest={deletePost}
            idProp={"id"}
            detailsRenderCardEnd={data => (
                <>
                    {data.usage != null && data.usages.length > 0 && (
                        <>
                            <DigitText.Title text={text.Usages} />

                            <DisplayPostUsages usages={data.usages} />
                        </>
                    )}
                    {(data.usages == null || data.usages.length) === 0 && (
                        <DigitText.Title text={text.NoUsages} />
                    )}
                </>
            )}
            formComponentData={generateEditComponentData(text)}
            formValidationSchema={generateValidationSchema(text)}
            createButtonText={text.CreatePost}
            backButtonText={text.Back}
            updateButtonText={() => text.Update}
        />
    );
};

export default Posts;
