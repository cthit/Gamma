import React from "react";

import {
    DigitCRUD,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { GROUP_NAME, GROUP_PRETTY_NAME } from "api/groups/props.groups.api";
import { deletePost } from "api/posts/delete.posts.api";
import { getPost, getPosts, getPostUsage } from "api/posts/get.posts.api";
import { addPost } from "api/posts/post.posts.api";
import {
    POST_ENGLISH,
    POST_ID,
    POST_SWEDISH,
    EMAIL_PREFIX
} from "api/posts/props.posts.api";
import { editPost } from "api/posts/put.posts.api";
import { ENGLISH_LANGUAGE, SWEDISH_LANGUAGE } from "api/utils/commonProps";

import DisplayGroupsTable from "common/elements/display-groups-table/DisplayGroupsTable.element";
import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import FiveZeroZero from "../../app/elements/five-zero-zero";
import FourOFour from "../four-o-four";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    keysText,
    validationSchema
} from "./Posts.options";
import translations from "./Posts.translations";

const Posts = () => {
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            useKeyTextsInUpperLabel
            formComponentData={keysComponentData(text)}
            formValidationSchema={validationSchema(text)}
            keysText={keysText(text)}
            formInitialValues={initialValues()}
            keysOrder={keysOrder()}
            path={"/posts"}
            name={"posts"}
            updateRequest={(id, data) => editPost(id, data)}
            createRequest={data => addPost(data)}
            readAllRequest={getPosts}
            readOneRequest={id => Promise.all([getPost(id), getPostUsage(id)])}
            createTitle={text.AddNewPost}
            tableProps={{
                titleText: text.Posts,
                startOrderBy: POST_SWEDISH,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            detailsButtonText={text.Details}
            deleteRequest={deletePost}
            idProp={POST_ID}
            detailsRenderCardEnd={data => (
                <>
                    {(data.usages == null || data.usages.length) === 0 && (
                        <DigitText.Title text={text.NoUsages} />
                    )}
                </>
            )}
            detailsRenderEnd={data => (
                <>
                    {data.usages != null && data.usages.length > 0 && (
                        <DisplayGroupsTable
                            margin={{ top: "16px" }}
                            groups={data.usages}
                            title={text.Usages}
                            columnsOrder={[GROUP_NAME, GROUP_PRETTY_NAME]}
                        />
                    )}
                </>
            )}
            createButtonText={text.CreatePost}
            backButtonText={text.Back}
            updateButtonText={() => text.Update}
            toastCreateSuccessful={data =>
                data[POST_SWEDISH] +
                "/" +
                data[POST_ENGLISH] +
                " " +
                text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingPostt}
            toastDeleteSuccessful={data =>
                data[POST_SWEDISH] +
                "/" +
                data[POST_ENGLISH] +
                " " +
                text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.PostDeletionFailed1 +
                " " +
                data[POST_SWEDISH] +
                "/" +
                data[POST_ENGLISH] +
                " " +
                text.PostDeletionFailed2
            }
            toastUpdateSuccessful={data =>
                data[POST_SWEDISH] +
                "/" +
                data[POST_ENGLISH] +
                " " +
                text.WasUpdatedSuccessfully
            }
            toastUpdateFailed={data =>
                text.PostUpdateFailed1 +
                " " +
                data[POST_SWEDISH] +
                "/" +
                data[POST_ENGLISH] +
                " " +
                text.PostUpdateFailed2
            }
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.AreYouSureYouWantToDelete +
                " " +
                data[POST_SWEDISH] +
                "/" +
                data[POST_ENGLISH] +
                "?"
            }
            updateTitle={data =>
                text.Update +
                " " +
                data[POST_SWEDISH] +
                "/" +
                data[POST_ENGLISH]
            }
            deleteButtonText={() => text.DeletePost}
            canDelete={data => !data.usages || data.usages.length === 0}
            detailsTitle={() => text.Details}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            useHistoryGoBackOnBack
        />
    );
};

export default Posts;
