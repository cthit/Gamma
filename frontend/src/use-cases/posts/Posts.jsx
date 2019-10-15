import React, { useEffect } from "react";

import {
    DigitCRUD,
    DigitText,
    useDigitTranslations,
    DigitTextField
} from "@cthit/react-digit-components";

import translations from "./Posts.translations";
import { getPost, getPosts, getPostUsage } from "../../api/posts/get.posts.api";
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
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";
import DisplayGroupsTable from "../../common/elements/display-groups-table/DisplayGroupsTable.element";
import { ID, NAME, PRETTY_NAME } from "../../api/groups/props.groups.api";

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
            upperLabel: text.SwedishInput,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[ENGLISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.EnglishInput,
            outlined: true,
            maxLength: 50
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

    const admin = useIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

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
                startOrderBy: "id",
                search: true
            }}
            detailsButtonText={text.Details}
            deleteRequest={deletePost}
            idProp={"id"}
            detailsRenderCardEnd={data => (
                <>
                    {(data.usages == null || data.usages.length) === 0 && (
                        <DigitText.Title text={text.NoUsages} />
                    )}
                </>
            )}
            detailsRenderEnd={data => (
                <div style={{ marginTop: "8px" }}>
                    {data.usages != null && data.usages.length > 0 && (
                        <DisplayGroupsTable
                            groups={data.usages}
                            title={text.Usages}
                            columnsOrder={[ID, NAME, PRETTY_NAME]}
                        />
                    )}
                </div>
            )}
            formComponentData={generateEditComponentData(text)}
            formValidationSchema={generateValidationSchema(text)}
            createButtonText={text.CreatePost}
            backButtonText={text.Back}
            updateButtonText={() => text.Update}
            toastCreateSuccessful={data =>
                data[SWEDISH_LANGUAGE] +
                "/" +
                data[ENGLISH_LANGUAGE] +
                " " +
                text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingPostt}
            toastDeleteSuccessful={data =>
                data[SWEDISH_LANGUAGE] +
                "/" +
                data[ENGLISH_LANGUAGE] +
                " " +
                text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.PostDeletionFailed1 +
                " " +
                data[SWEDISH_LANGUAGE] +
                "/" +
                data[ENGLISH_LANGUAGE] +
                " " +
                text.PostDeletionFailed2
            }
            toastUpdateSuccessful={data =>
                data[SWEDISH_LANGUAGE] +
                "/" +
                data[ENGLISH_LANGUAGE] +
                " " +
                text.WasUpdatedSuccessfully
            }
            toastUpdateFailed={data =>
                text.PostDeletionFailed1 +
                " " +
                data[SWEDISH_LANGUAGE] +
                "/" +
                data[ENGLISH_LANGUAGE] +
                " " +
                text.PostDeletionFailed2
            }
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.AreYouSureYouWantToDelete +
                " " +
                data[SWEDISH_LANGUAGE] +
                "/" +
                data[ENGLISH_LANGUAGE] +
                "?"
            }
            updateTitle={data =>
                text.Update +
                " " +
                data[SWEDISH_LANGUAGE] +
                "/" +
                data[ENGLISH_LANGUAGE]
            }
            deleteButtonText={() => text.DeletePost}
            detailsTitle={() => text.Details}
        />
    );
};

export default Posts;
