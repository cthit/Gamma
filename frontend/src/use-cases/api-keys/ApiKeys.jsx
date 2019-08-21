import React, { useEffect } from "react";
import {
    DigitCRUD,
    DigitTextArea,
    DigitTextField,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { getApiKey, getApiKeys } from "../../api/api-keys/get.api-keys.api";
import { addApiKey } from "../../api/api-keys/post.api-keys.api";
import * as yup from "yup";
import { deleteApiKey } from "../../api/api-keys/delete.api-keys.api";
import translations from "./ApiKeys.translations";

const ApiKeys = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

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
            path={"/api-keys"}
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
                        upperLabel: text.Name
                    }
                },
                descriptionSv: {
                    component: DigitTextArea,
                    componentProps: {
                        numbersOnly: true,
                        outlined: true,
                        rows: 3,
                        upperLabel: text.Swedish
                    }
                },
                descriptionEn: {
                    component: DigitTextArea,
                    componentProps: {
                        numbersOnly: true,
                        outlined: true,
                        rows: 3,
                        upperLabel: text.English
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
                titleText: text.ApiKeysTitle
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
