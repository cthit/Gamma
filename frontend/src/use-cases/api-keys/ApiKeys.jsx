import {
    initialValues,
    keysComponentData,
    keysOrder,
    keysText,
    validationSchema
} from "./ApiKeys.options";
import React, { useEffect, useState } from "react";

import {
    DigitButton,
    DigitCRUD,
    DigitLayout,
    DigitLoading,
    DigitText,
    useDigitCustomDialog,
    useDigitDialog,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { deleteApiKey } from "api/api-keys/delete.api-keys.api";
import {
    getApiKey,
    getApiKeys,
    getApiKeyTypes
} from "api/api-keys/get.api-keys.api";
import { addApiKey, resetApiKey } from "api/api-keys/post.api-keys.api";
import { API_ID, API_NAME, API_SECRET } from "api/api-keys/props.api-keys.api";

import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import FiveZeroZero from "../../app/elements/five-zero-zero";
import FourOFour from "../four-o-four";
import translations from "./ApiKeys.translations.json";

const ApiKeys = () => {
    const [text] = useDigitTranslations(translations);
    const [showDialog] = useDigitDialog();
    const [showCustomDialog] = useDigitCustomDialog({
        title: text.YourApiKeySecret,
        renderButtons: confirm => (
            <DigitButton text={text.Close} onClick={confirm} />
        )
    });
    const [types, setTypes] = useState(null);

    useEffect(() => {
        getApiKeyTypes().then(response => setTypes(response.data));
    }, []);

    if (types == null) {
        return (
            <DigitLayout.Center size={{ height: "200px" }}>
                <DigitLoading loading />
            </DigitLayout.Center>
        );
    }

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            keysText={keysText(text)}
            keysOrder={keysOrder()}
            formComponentData={keysComponentData(text, types)}
            formValidationSchema={validationSchema(text)}
            formInitialValues={initialValues()}
            readOneRequest={getApiKey}
            readAllRequest={getApiKeys}
            deleteRequest={deleteApiKey}
            createRequest={newApi =>
                addApiKey({
                    prettyName: newApi.prettyName,
                    keyType: newApi.keyType,
                    description: {
                        sv: newApi.descriptionSv,
                        en: newApi.descriptionEn
                    }
                })
            }
            onCreate={response => {
                const secret = response.data;
                showCustomDialog({
                    renderMain: () => (
                        <>
                            <DigitText.Text bold alignCenter text={secret} />
                            <DigitText.Text
                                text={text.YourApiKeySecretDescription}
                            />
                        </>
                    )
                });
            }}
            idProp={API_ID}
            name={"api"}
            path={"/access-keys"}
            tableProps={{
                titleText: text.ApiKeysTitle,
                startOrderBy: API_NAME,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            detailsButtonText={text.Details}
            dialogDeleteConfirm={data => text.Delete + " " + data[API_NAME]}
            dialogDeleteTitle={() => text.DialogDeleteTitle}
            dialogDeleteDescription={data =>
                text.DialogDeleteDescription1 +
                data[API_NAME] +
                text.DialogDeleteDescription2
            }
            toastDeleteSuccessful={data =>
                text.ToastDelete1 + data[API_NAME] + text.ToastDeleteSuccessful2
            }
            toastDeleteFailed={data =>
                text.ToastDelete1 + data[API_NAME] + text.ToastDeleteFailed2
            }
            backButtonText={text.Back}
            deleteButtonText={data => text.Delete + " " + data[API_NAME]}
            createButtonText={text.Create}
            createTitle={text.CreateNewApiKey}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            detailsTitle={one => one[API_NAME]}
            createSubtitle={text.CreateApiKeySubtitle}
            createProps={{
                size: { maxWidth: "400px" }
            }}
            toastCreateSuccessful={() => text.ApiKeyCreated}
            toastCreateFailed={() => text.ApiKeyCreateFailed}
            useKeyTextsInUpperLabel
            detailsRenderCardEnd={data => (
                <DigitButton
                    text={text.ResetToken}
                    outlined
                    onClick={() =>
                        showDialog({
                            title: text.AreYouSure,
                            description: text.ResetTokenDescription,
                            confirmButtonText: text.ResetToken,
                            cancelButtonText: text.Cancel,
                            onConfirm: () =>
                                resetApiKey(data.id).then(response => {
                                    const secret = response.data;
                                    showCustomDialog({
                                        renderMain: () => (
                                            <>
                                                <DigitText.Text
                                                    bold
                                                    alignCenter
                                                    text={secret}
                                                />
                                                <DigitText.Text
                                                    text={
                                                        text.YourApiKeySecretDescription
                                                    }
                                                />
                                            </>
                                        )
                                    });
                                })
                        })
                    }
                />
            )}
        />
    );
};

export default ApiKeys;
