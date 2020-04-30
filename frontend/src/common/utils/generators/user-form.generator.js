import React from "react";
import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_GROUPS,
    USER_ID,
    USER_LANGUAGE,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD,
    USER_PHONE,
    USER_AGREEMENT
} from "../../../api/users/props.users.api";
import * as yup from "yup";
import {
    DigitSelect,
    DigitSwitch,
    DigitTextField
} from "@cthit/react-digit-components";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../../api/utils/commonProps";
import DisplayGroupsTable from "../../elements/display-groups-table/DisplayGroupsTable.element";
import { GROUP_PRETTY_NAME } from "../../../api/groups/props.groups.api";

export function generateUserKeysTexts(text) {
    const output = {};

    output[USER_ID] = text.Id;
    output[USER_CID] = text.Cid;
    output[USER_FIRST_NAME] = text.FirstName;
    output[USER_LAST_NAME] = text.LastName;
    output[USER_NICK] = text.Nick;
    output[USER_EMAIL] = text.Email;
    output[USER_ACCEPTANCE_YEAR] = text.AcceptanceYear;
    output[USER_LANGUAGE] = text.Language;
    output[USER_CID] = text.Cid;
    output[USER_AGREEMENT] = text.AcceptUserAgreement;
    output[USER_PASSWORD] = text.Password;
    output[USER_PHONE] = text.Phone;

    return output;
}

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

export const generateUserKeyOrder = () => [
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_EMAIL,
    USER_ACCEPTANCE_YEAR,
    USER_PHONE,
    USER_LANGUAGE,
    USER_GROUPS
];

export function generateUserInitialValues() {
    const output = {};

    output[USER_FIRST_NAME] = "";
    output[USER_LAST_NAME] = "";
    output[USER_NICK] = "";
    output[USER_EMAIL] = "";
    output[USER_ACCEPTANCE_YEAR] = "";
    output[USER_LANGUAGE] = "";
    output[USER_AGREEMENT] = false;
    output[USER_CID] = "";
    output[USER_PASSWORD] = "";
    output[USER_PHONE] = "";

    return output;
}

export function generateUserValidationSchema(
    text,
    forceUserAgreement = false,
    forceCid = false,
    forcePassword = false
) {
    const schema = {};
    schema[USER_FIRST_NAME] = yup.string().required(text.FieldRequired);
    schema[USER_LAST_NAME] = yup.string().required(text.FieldRequired);
    schema[USER_NICK] = yup.string().required(text.FieldRequired);
    schema[USER_EMAIL] = yup.string().required(text.FieldRequired);
    schema[USER_ACCEPTANCE_YEAR] = yup.number().required(text.FieldRequired);
    schema[USER_PHONE] = yup.string();
    schema[USER_AGREEMENT] = forceUserAgreement
        ? yup
              .boolean()
              .oneOf([true])
              .required()
        : yup.boolean();
    schema[USER_CID] = forceCid
        ? yup
              .string()
              .min(4)
              .max(12)
              .required(text.FieldRequired)
        : yup
              .string()
              .min(4)
              .max(12);
    schema[USER_PASSWORD] = forcePassword
        ? yup
              .string()
              .min(8, text.PasswordTooShort)
              .required(text.FieldRequired)
        : yup.string();

    return yup.object().shape(schema);
}

export function generateUserEditComponentData(text) {
    const componentData = {};

    componentData[USER_FIRST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FirstName,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[USER_LAST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.LastName,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[USER_NICK] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Nick,
            outlined: true,
            maxLength: 20
        }
    };

    componentData[USER_EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            outlined: true,
            maxLength: 100
        }
    };

    componentData[USER_ACCEPTANCE_YEAR] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.AcceptanceYear,
            lowerLabel: text.AcceptanceYearLowerLabel,
            valueToTextMap: _generateAcceptanceYears(),
            reverse: true,
            outlined: true
        }
    };

    componentData[USER_CID] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Cid,
            outlined: true,
            maxLength: 12
        }
    };

    componentData[USER_AGREEMENT] = {
        component: DigitSwitch,
        componentProps: {
            label: text.AcceptUserAgreement,
            primary: true
        }
    };

    componentData[USER_PHONE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Phone,
            outlined: true,
            maxLength: 15
        }
    };

    const languageOptions = {};
    languageOptions[SWEDISH_LANGUAGE] = "Svenska";
    languageOptions[ENGLISH_LANGUAGE] = "English";

    componentData[USER_LANGUAGE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Language,
            lowerLabel: text.LanguageLowerLabel,
            valueToTextMap: languageOptions,
            outlined: true
        }
    };

    componentData[USER_PASSWORD] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Password,
            outlined: true,
            password: true
        }
    };

    return componentData;
}

export function generateUserCustomDetailsRenders(text, ignoreGroups) {
    const output = {};

    if (ignoreGroups) {
        output[USER_GROUPS] = () => null;
    } else {
        output[USER_GROUPS] = data =>
            data[USER_GROUPS] != null ? (
                <DisplayGroupsTable
                    groups={data[USER_GROUPS]}
                    title={text.Groups}
                    columnsOrder={[GROUP_PRETTY_NAME]}
                />
            ) : null;
    }

    return output;
}
