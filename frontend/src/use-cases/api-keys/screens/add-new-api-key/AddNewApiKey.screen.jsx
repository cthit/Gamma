import React, { useEffect } from "react";
import * as yup from "yup";
import {
    DigitTranslations,
    DigitLayout,
    DigitEditData,
    DigitTextField,
    DigitText
} from "@cthit/react-digit-components";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../../../api/utils/commonProps";
import {
    API_KEY_NAME,
    DESCRIPTION
} from "../../../../api/api-keys/props.api-keys.api";

import translations from "./AddNewClient.screen.translations";

function generateValidationSchema(text) {
    const shape = {};

    shape[API_KEY_NAME] = yup.string().required(text.FieldReqired);
    shape[DESCRIPTION + "-" + SWEDISH_LANGUAGE] = yup
        .string()
        .required(text.FieldReqired);
    shape[DESCRIPTION + "-" + ENGLISH_LANGUAGE] = yup
        .string()
        .required(text.FieldReqired);

    return yup.object().shape(shape);
}

function generateKeysComponentData(text) {
    const componentData = {};

    componentData[API_KEY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.ApiKeyName,
            maxLength: 30,
            filled: true
        }
    };

    componentData[DESCRIPTION + "-" + SWEDISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.DescriptionSv,
            maxLength: 500,
            filled: true
        }
    };

    componentData[DESCRIPTION + "-" + ENGLISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.DescriptionEn,
            maxLength: 500,
            filled: true
        }
    };

    return componentData;
}

function generateInitialValues() {
    const output = {};

    output[API_KEY_NAME] = "";

    output[DESCRIPTION + "-" + SWEDISH_LANGUAGE] = "";
    output[DESCRIPTION + "-" + ENGLISH_LANGUAGE] = "";

    return output;
}

const AddNewClient = ({
    gammaLoadingFinished,
    openDialog,
    closeDialog,
    addApiKey
}) => {
    useEffect(() => {
        gammaLoadingFinished();
    }, []);
    return (
        <DigitTranslations
            translations={translations}
            render={text => (
                <DigitLayout.Center>
                    <DigitEditData
                        isInitialValid={false}
                        validationSchema={generateValidationSchema(text)}
                        onSubmit={values => {
                            const apiKey = {};
                            apiKey[API_KEY_NAME] = values[API_KEY_NAME];
                            apiKey[DESCRIPTION] = {};
                            apiKey[DESCRIPTION][SWEDISH_LANGUAGE] =
                                values[DESCRIPTION + "-" + SWEDISH_LANGUAGE];
                            apiKey[DESCRIPTION][ENGLISH_LANGUAGE] =
                                values[DESCRIPTION + "-" + ENGLISH_LANGUAGE];

                            addApiKey(apiKey).then(response => {
                                openDialog({
                                    title: text.GeneratedAPIKey,
                                    renderMain: () => (
                                        <DigitText.Text
                                            text={response.data.apiKey}
                                        />
                                    )
                                });
                            });
                        }}
                        keysComponentData={generateKeysComponentData(text)}
                        initialValues={generateInitialValues()}
                        keysOrder={[
                            API_KEY_NAME,
                            DESCRIPTION + "-" + SWEDISH_LANGUAGE,
                            DESCRIPTION + "-" + ENGLISH_LANGUAGE
                        ]}
                        titleText={text.CreateApiKey}
                        submitText={text.SubmitApiKey}
                    />
                </DigitLayout.Center>
            )}
        />
    );
};

export default AddNewClient;
