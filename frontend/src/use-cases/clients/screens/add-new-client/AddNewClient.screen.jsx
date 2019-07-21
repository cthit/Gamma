import React, { useEffect } from "react";
import * as yup from "yup";
import {
    DigitTranslations,
    DigitLayout,
    DigitEditData,
    DigitTextField,
    DigitButton,
    DigitText
} from "@cthit/react-digit-components";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../../../api/utils/commonProps";
import {
    CLIENT_NAME,
    CLIENT_REDIRECT,
    DESCRIPTION
} from "../../../../api/clients/props.clients.api";

import translations from "./AddNewClient.screen.translations";

function generateValidationSchema(text) {
    const shape = {};

    shape[CLIENT_NAME] = yup.string().required(text.FieldRequired);
    shape[CLIENT_REDIRECT] = yup.string().required(text.FieldRequired);
    shape[DESCRIPTION + "-" + SWEDISH_LANGUAGE] = yup
        .string()
        .required(text.FieldRequired);
    shape[DESCRIPTION + "-" + ENGLISH_LANGUAGE] = yup
        .string()
        .required(text.FieldRequired);

    return yup.object().shape(shape);
}

function generateKeysComponentData(text) {
    const componentData = {};

    componentData[CLIENT_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.ClientName,
            maxLength: 30,
            filled: true
        }
    };

    componentData[CLIENT_REDIRECT] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.ClientRedirect,
            maxLength: 256,
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

    output[CLIENT_NAME] = "";
    output[CLIENT_REDIRECT] = "";

    output[DESCRIPTION + "-" + SWEDISH_LANGUAGE] = "";
    output[DESCRIPTION + "-" + ENGLISH_LANGUAGE] = "";

    return output;
}

const AddNewClient = ({
    gammaLoadingFinished,
    openDialog,
    closeDialog,
    addClient
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
                            const client = {};
                            client[CLIENT_NAME] = values[CLIENT_NAME];
                            client[CLIENT_REDIRECT] = values[CLIENT_REDIRECT];
                            client[DESCRIPTION] = {};
                            client[DESCRIPTION][SWEDISH_LANGUAGE] =
                                values[DESCRIPTION + "-" + SWEDISH_LANGUAGE];
                            client[DESCRIPTION][ENGLISH_LANGUAGE] =
                                values[DESCRIPTION + "-" + ENGLISH_LANGUAGE];

                            addClient(client).then(response => {
                                openDialog({
                                    title: text.YourClientSecret,
                                    renderMain: () => (
                                        <DigitText.Text
                                            text={response.data.clientSecret}
                                        />
                                    )
                                    // renderButtons: () => (
                                    //     <DigitButton
                                    //         text={text.CloseDialog}
                                    //         onClick={closeDialog}
                                    //     />
                                    // )
                                });
                            });
                        }}
                        keysComponentData={generateKeysComponentData(text)}
                        initialValues={generateInitialValues()}
                        keysOrder={[
                            CLIENT_NAME,
                            CLIENT_REDIRECT,
                            DESCRIPTION + "-" + SWEDISH_LANGUAGE,
                            DESCRIPTION + "-" + ENGLISH_LANGUAGE
                        ]}
                        titleText={text.CreateClient}
                        submitText={text.SubmitClient}
                    />
                </DigitLayout.Center>
            )}
        />
    );
};

export default AddNewClient;
