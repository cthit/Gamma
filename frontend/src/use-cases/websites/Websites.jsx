import React, { useEffect } from "react";
import {
    DigitCRUD,
    DigitTextField,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { getWebsite, getWebsites } from "../../api/websites/get.websites.api";
import { editWebsite } from "../../api/websites/put.websites.api";
import { deleteWebsite } from "../../api/websites/delete.websites.api";
import { addWebsite } from "../../api/websites/post.websites.api";
import translations from "./Websites.translations";
import * as yup from "yup";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";

const Websites = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, [dispatch]);

    const admin = useIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            name={"websites"}
            path={"/websites"}
            readAllRequest={getWebsites}
            readOneRequest={getWebsite}
            updateRequest={editWebsite}
            deleteRequest={deleteWebsite}
            createRequest={addWebsite}
            tableProps={{
                titleText: text.Websites,
                startOrderBy: "name",
                search: true
            }}
            keysOrder={["id", "name", "prettyName"]}
            keysText={{
                id: text.Id,
                name: text.Swedish,
                prettyName: text.English
            }}
            idProp={"id"}
            formComponentData={{
                name: {
                    component: DigitTextField,
                    componentProps: {
                        upperLabel: text.PrettyName,
                        outlined: true,
                        maxLength: 50
                    }
                },
                prettyName: {
                    component: DigitTextField,
                    componentProps: {
                        upperLabel: text.PrettyName,
                        outlined: true,
                        maxLength: 50
                    }
                }
            }}
            formValidationSchema={yup.object().shape({
                name: yup.string().required(),
                prettyName: yup.string().required()
            })}
            formInitialValues={{
                name: "",
                prettyName: ""
            }}
            backButtonText={text.Back}
            detailsButtonText={text.Details}
            deleteButtonText={data => text.Delete + " " + data.prettyName}
            detailsTitle={data => data.prettyName}
            updateTitle={data => text.Update + " " + data.prettyName}
            createTitle={text.CreateWebsite}
            createButtonText={text.CreateWebsite}
            updateButtonText={data => text.Update + " " + data.prettyName}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.AreYouSureYouWantToDelete + " " + data.prettyName
            }
            toastCreateSuccessful={data =>
                data.prettyName + " " + text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingWebsite}
            toastDeleteSuccessful={data =>
                data.prettyName + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.FailedDeleting + " " + data.prettyName
            }
            toastUpdateSuccessful={data =>
                data.prettyName + " " + text.WasUpdatedSuccessfully
            }
            toastUpdateFailed={data =>
                text.WebsiteUpdateFailed1 +
                " " +
                data.prettyName +
                " " +
                text.WebsiteUpdateFailed2
            }
        />
    );
};

export default Websites;
