import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    LANGUAGE,
    LAST_NAME,
    NICK,
    WEBSITES
} from "../../../api/users/props.users.api";
import * as yup from "yup";
import { DigitSelect, DigitTextField } from "@cthit/react-digit-components";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../../api/utils/commonProps";
import EditWebsites from "../../views/edit-websites";

export function generateUserKeysTexts(text) {
    const output = {};

    output[CID] = text.Cid;
    output[FIRST_NAME] = text.FirstName;
    output[LAST_NAME] = text.LastName;
    output[NICK] = text.Nick;
    output[EMAIL] = text.Email;
    output[ACCEPTANCE_YEAR] = text.AcceptanceYear;
    output[LANGUAGE] = text.Language;

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

export function generateUserValidationSchema(text, includeCidAndPassword) {
    const schema = {};
    schema[FIRST_NAME] = yup.string().required(text.FieldRequired);
    schema[LAST_NAME] = yup.string().required(text.FieldRequired);
    schema[NICK] = yup.string().required(text.FieldRequired);
    schema[EMAIL] = yup.string().required(text.FieldRequired);
    schema[ACCEPTANCE_YEAR] = yup.number().required(text.FieldRequired);
    schema[WEBSITES] = yup.array().of(yup.object());

    return yup.object().shape(schema);
}

export function generateUserEditComponentData(text, availableWebsites) {
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
            valueToTextMap: _generateAcceptanceYears(),
            reverse: true,
            outlined: true
        }
    };

    const languageOptions = {};
    languageOptions[SWEDISH_LANGUAGE] = "Svenska";
    languageOptions[ENGLISH_LANGUAGE] = "English";

    componentData[LANGUAGE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Language,
            valueToTextMap: languageOptions,
            outlined: true
        }
    };

    componentData[WEBSITES] = {
        array: true,
        component: EditWebsites,
        componentProps: {
            availableWebsites: availableWebsites
        }
    };

    return componentData;
}

export function generateUserCustomDetailsRenders() {
    const output = {};

    output[WEBSITES] = data => null;

    return output;
}
