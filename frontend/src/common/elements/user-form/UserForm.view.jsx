import React from "react";
import * as yup from "yup";

import {
    DigitEditData,
    DigitSelect,
    DigitTextField,
    DigitTranslations
} from "@cthit/react-digit-components";

import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD,
    USER_LANGUAGE
} from "api/users/props.users.api";
import { ENGLISH_LANGUAGE, SWEDISH_LANGUAGE } from "api/utils/commonProps";

import translations from "./UserForm.view.translations.json";

function _getCurrentYear() {
    return new Date().getFullYear() + "";
}

function _generateAcceptanceYears() {
    const output = {};
    const startYear = 2001;
    const currentYear = _getCurrentYear();
    for (var i = currentYear; i >= startYear; i--) {
        output["" + i] = i;
    }

    return output;
}

function generateValidationSchema(text, includeCidAndPassword) {
    const schema = {};
    schema[USER_FIRST_NAME] = yup.string().required(text.FieldRequired);
    schema[USER_LAST_NAME] = yup.string().required(text.FieldRequired);
    schema[USER_NICK] = yup.string().required(text.FieldRequired);
    schema[USER_EMAIL] = yup.string().required(text.FieldRequired);
    schema[USER_ACCEPTANCE_YEAR] = yup.number().required(text.FieldRequired);

    if (includeCidAndPassword) {
        schema[USER_CID] = yup.string().required(text.FieldRequired);
        schema[USER_PASSWORD] = yup.string().required(text.FieldRequired);
    }

    return yup.object().shape(schema);
}

function generateEditComponentData(text, includeCidAndPassword) {
    const componentData = {};

    componentData[USER_FIRST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FirstName,
            outlined: true
        }
    };

    componentData[USER_LAST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.LastName,
            outlined: true
        }
    };

    componentData[USER_NICK] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Nick,
            outlined: true
        }
    };

    componentData[USER_EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            outlined: true
        }
    };

    componentData[USER_ACCEPTANCE_YEAR] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.AcceptanceYear,
            valueToTextMap: _generateAcceptanceYears(),
            reverse: true,
            outlined: true
        }
    };

    const languageOptions = {};
    languageOptions[SWEDISH_LANGUAGE] = "Svenska";
    languageOptions[ENGLISH_LANGUAGE] = "English";

    componentData[USER_LANGUAGE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Language,
            valueToTextMap: languageOptions,
            outlined: true
        }
    };

    if (includeCidAndPassword) {
        componentData[USER_CID] = {
            component: DigitTextField,
            componentProps: {
                upperLabel: text.Cid,
                outlined: true
            }
        };

        componentData[USER_PASSWORD] = {
            component: DigitTextField,
            componentProps: {
                upperLabel: text.Password,
                password: true,
                outlined: true
            }
        };
    }
    return componentData;
}

function getKeysOrder(includeCidAndPassword) {
    const output = [
        USER_FIRST_NAME,
        USER_LAST_NAME,
        USER_NICK,
        USER_EMAIL,
        USER_ACCEPTANCE_YEAR,
        USER_LANGUAGE
    ];

    if (includeCidAndPassword) {
        output.push(USER_CID);
        output.push(USER_PASSWORD);
    }

    return output;
}

const UserForm = ({
    initialValues,
    includeCidAndPassword,
    onSubmit,
    titleText,
    submitText
}) => (
    <DigitTranslations
        translations={translations}
        render={text => (
            <DigitEditData
                titleText={titleText}
                submitText={submitText}
                initialValues={{
                    language: ENGLISH_LANGUAGE,
                    ...initialValues
                }}
                onSubmit={(values, actions) => {
                    actions.setSubmitting(false);
                    onSubmit(values, actions);
                }}
                validationSchema={generateValidationSchema(
                    text,
                    includeCidAndPassword
                )}
                keysOrder={getKeysOrder(includeCidAndPassword)}
                keysComponentData={generateEditComponentData(
                    text,
                    includeCidAndPassword
                )}
            />
        )}
    />
);

export default UserForm;
