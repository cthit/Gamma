import React from "react";
import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    GROUPS,
    ID,
    LANGUAGE,
    LAST_NAME,
    NICK,
    PASSWORD,
    USER_AGREEMENT
} from "../../../api/users/props.users.api";
import * as yup from "yup";
import {
    DigitSelect,
    DigitSwitch,
    DigitTextField,
    DigitText
} from "@cthit/react-digit-components";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../../api/utils/commonProps";
import DisplayGroupsTable from "../../elements/display-groups-table/DisplayGroupsTable.element";
import { PRETTY_NAME } from "../../../api/groups/props.groups.api";

export function generateUserKeysTexts(text) {
    const output = {};

    output[ID] = text.Id;
    output[CID] = text.Cid;
    output[FIRST_NAME] = text.FirstName;
    output[LAST_NAME] = text.LastName;
    output[NICK] = text.Nick;
    output[EMAIL] = text.Email;
    output[ACCEPTANCE_YEAR] = text.AcceptanceYear;
    output[LANGUAGE] = text.Language;
    output[CID] = text.Cid;
    output[USER_AGREEMENT] = text.AcceptUserAgreement;
    output[PASSWORD] = text.Password;

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
    FIRST_NAME,
    LAST_NAME,
    NICK,
    EMAIL,
    ACCEPTANCE_YEAR,
    LANGUAGE,
    GROUPS
];

export function generateUserInitialValues() {
    const output = {};

    output[FIRST_NAME] = "";
    output[LAST_NAME] = "";
    output[NICK] = "";
    output[EMAIL] = "";
    output[ACCEPTANCE_YEAR] = "";
    output[LANGUAGE] = "";
    output[USER_AGREEMENT] = false;
    output[CID] = "";
    output[PASSWORD] = "";

    return output;
}

export function generateUserValidationSchema(
    text,
    forceUserAgreement = false,
    forceCid = false,
    forcePassword = false
) {
    const schema = {};
    schema[FIRST_NAME] = yup.string().required(text.FieldRequired);
    schema[LAST_NAME] = yup.string().required(text.FieldRequired);
    schema[NICK] = yup.string().required(text.FieldRequired);
    schema[EMAIL] = yup.string().required(text.FieldRequired);
    schema[ACCEPTANCE_YEAR] = yup.number().required(text.FieldRequired);
    schema[USER_AGREEMENT] = forceUserAgreement
        ? yup
              .boolean()
              .oneOf([true])
              .required()
        : yup.boolean();
    schema[CID] = forceCid
        ? yup.string().required(text.FieldRequired)
        : yup.string();
    schema[PASSWORD] = forcePassword
        ? yup
              .string()
              .min(8, text.PasswordTooShort)
              .required(text.FieldRequired)
        : yup.string();

    return yup.object().shape(schema);
}

export function generateUserEditComponentData(text) {
    const componentData = {};

    componentData[FIRST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FirstName,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[LAST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.LastName,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[NICK] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Nick,
            outlined: true,
            maxLength: 20
        }
    };

    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            outlined: true,
            maxLength: 100
        }
    };

    componentData[ACCEPTANCE_YEAR] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.AcceptanceYear,
            lowerLabel: text.AcceptanceYearLowerLabel,
            valueToTextMap: _generateAcceptanceYears(),
            reverse: true,
            outlined: true
        }
    };

    componentData[CID] = {
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

    const languageOptions = {};
    languageOptions[SWEDISH_LANGUAGE] = "Svenska";
    languageOptions[ENGLISH_LANGUAGE] = "English";

    componentData[LANGUAGE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Language,
            lowerLabel: text.LanguageLowerLabel,
            valueToTextMap: languageOptions,
            outlined: true
        }
    };

    componentData[PASSWORD] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Password,
            lowerLabel: text.PasswordAtleast,
            password: true,
            outlined: true
        }
    };

    return componentData;
}

export function generateUserCustomDetailsRenders(text, ignoreGroups) {
    const output = {};

    if (ignoreGroups) {
        output[GROUPS] = data => null;
    } else {
        output[GROUPS] = data =>
            data.groups != null ? (
                <DisplayGroupsTable
                    groups={data.groups}
                    title={text.Groups}
                    columnsOrder={[PRETTY_NAME]}
                />
            ) : (
                <DigitText.Text text={text.NoGroups} />
            );
    }

    return output;
}
