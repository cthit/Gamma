import React from "react";
import {
    DigitButton,
    DigitCRUD,
    DigitText,
    useDigitCustomDialog,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { getApiKey, getApiKeys } from "../../api/api-keys/get.api-keys.api";
import { addApiKey } from "../../api/api-keys/post.api-keys.api";
import { deleteApiKey } from "../../api/api-keys/delete.api-keys.api";
import translations from "./ApiKeys.translations";
import InsufficientAccess from "../../common/views/insufficient-access";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    keysText,
    validationSchema
} from "./ApiKeys.options";
import {
    API_ID,
    API_NAME,
    API_SECRET
} from "../../api/api-keys/props.api-keys.api";

const ApiKeys = () => {
    const [text] = useDigitTranslations(translations);
    const [showDialog] = useDigitCustomDialog({
        title: text.YourApiKeySecret,
        renderButtons: confirm => (
            <DigitButton text={text.Close} onClick={confirm} />
        )
    });

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            keysText={keysText(text)}
            keysOrder={keysOrder()}
            formComponentData={keysComponentData(text)}
            formValidationSchema={validationSchema(text)}
            formInitialValues={initialValues()}
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
            onCreate={response => {
                const secret = response.data[API_SECRET];
                showDialog({
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
                padding: "0px"
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
        />
    );
};

export default ApiKeys;
