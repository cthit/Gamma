import React, { useEffect } from "react";
import {
    DigitCRUD,
    DigitTextArea,
    DigitTextField,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { useDispatch } from "react-redux";
import { deltaLoadingFinished } from "../../app/views/delta-loading/DeltaLoading.view.action-creator";
import { getApiKey, getApiKeys } from "../../api/api-keys/get.api-keys.api";
import { addApiKey } from "../../api/api-keys/post.api-keys.api";
import * as yup from "yup";
import { deleteApiKey } from "../../api/api-keys/delete.api-keys.api";
import translations from "./ApiKeys.translations";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";

const ApiKeys = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(deltaLoadingFinished());
    }, []);

    const admin = useIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            readOneRequest={getApiKey}
            readAllRequest={getApiKeys}
            deleteRequest={deleteApiKey}
            createRequest={newApi =>
                addApiKey({
                    name: newApi.name,
                    description: {
                        sv: newApi.descriptionSv,
                        en: newApi.descriptionEn
                    }
                })
            }
            idProp={"id"}
            name={"api"}
            path={"/access-keys"}
            keysText={{
                name: text.Name,
                descriptionSv: text.Swedish,
                descriptionEn: text.English
            }}
            keysOrder={["name", "descriptionSv", "descriptionEn"]}
            formComponentData={{
                name: {
                    component: DigitTextField,
                    componentProps: {
                        outlined: true,
                        upperLabel: text.Name,
                        maxLength: 50
                    }
                },
                descriptionSv: {
                    component: DigitTextArea,
                    componentProps: {
                        numbersOnly: true,
                        outlined: true,
                        rows: 3,
                        upperLabel: text.Swedish,
                        maxLength: 500
                    }
                },
                descriptionEn: {
                    component: DigitTextArea,
                    componentProps: {
                        numbersOnly: true,
                        outlined: true,
                        rows: 3,
                        upperLabel: text.English,
                        maxLength: 500
                    }
                }
            }}
            formValidationSchema={yup.object().shape({
                name: yup.string().required(),
                descriptionSv: yup.string().required(),
                descriptionEn: yup.string().required()
            })}
            formInitialValues={{
                name: "",
                descriptionSv: "",
                descriptionEn: ""
            }}
            tableProps={{
                titleText: text.ApiKeysTitle,
                startOrderBy: "name",
                search: true
            }}
            detailsButtonText={text.Details}
            dialogDeleteConfirm={data => text.Delete + " " + data.name}
            dialogDeleteTitle={() => text.DialogDeleteTitle}
            dialogDeleteDescription={data =>
                text.DialogDeleteDescription1 +
                data.name +
                text.DialogDeleteDescription2
            }
            toastDeleteSuccessful={data =>
                text.ToastDelete1 + data.name + text.ToastDeleteSuccessful2
            }
            toastDeleteFailed={data =>
                text.ToastDelete1 + data.name + text.ToastDeleteFailed2
            }
            backButtonText={text.Back}
            deleteButtonText={data => text.Delete + " " + data.name}
            createButtonText={text.Create}
            createTitle={text.CreateNewApiKey}
        />
    );
};

export default ApiKeys;
