import React from "react";
import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_GROUPS,
    USER_LANGUAGE,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD,
    USER_PHONE,
    USER_AGREEMENT
} from "../../../api/users/props.users.api";
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
