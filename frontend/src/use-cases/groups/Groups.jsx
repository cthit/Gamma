import React, { useEffect, useState } from "react";

import {
    DigitCRUD,
    useDigitTranslations,
    DigitTextField,
    DigitDatePicker,
    DigitSelect,
    DigitTextArea
} from "@cthit/react-digit-components";
import translations from "./Groups.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { getGroup, getGroupsMinified } from "../../api/groups/get.groups.api";
import {
    EMAIL,
    ID,
    NAME,
    PRETTY_NAME,
    SUPER_GROUP
} from "../../api/groups/props.groups.api";
import { editGroup } from "../../api/groups/put.groups.api";

import * as yup from "yup";
import { getSuperGroups } from "../../api/super-groups/get.super-groups.api";
import { addGroup } from "../../api/groups/post.groups.api";

const DESCRIPTION_SV = "descriptionSv";
const DESCRIPTION_EN = "descriptionEn";
const FUNCTION_SV = "functionSv";
const FUNCTION_EN = "functionEn";

function generateValidationSchema(text) {
    const schema = {};

    schema[NAME] = yup.string().required();
    schema[PRETTY_NAME] = yup.string().required();
    schema[EMAIL] = yup.string().required();

    schema[DESCRIPTION_SV] = yup.string().required();
    schema[DESCRIPTION_EN] = yup.string().required();

    schema[FUNCTION_SV] = yup.string().required();
    schema[FUNCTION_EN] = yup.string().required();

    return yup.object().shape(schema);
}

function generateEditComponentData(text, superGroups = []) {
    const componentData = {};

    componentData[NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            maxLength: 50,
            filled: true
        }
    };

    componentData[PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            maxLength: 50,
            filled: true
        }
    };

    componentData[DESCRIPTION_SV] = {
        component: DigitTextArea,
        componentProps: {
            upperLabel: text.DescriptionSv,
            maxLength: 500,
            rows: 5,
            maxRows: 10,
            filled: true
        }
    };

    componentData[DESCRIPTION_EN] = {
        component: DigitTextArea,
        componentProps: {
            upperLabel: text.DescriptionEn,
            maxLength: 500,
            rows: 5,
            maxRows: 10,
            filled: true
        }
    };

    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            maxLength: 100,
            filled: true
        }
    };

    componentData[FUNCTION_SV] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionSv,
            maxLength: 100,
            filled: true
        }
    };

    componentData[FUNCTION_EN] = {
        component: DigitTextField,
        componentProps: {
            maxLength: 100,
            upperLabel: text.FunctionEn,
            filled: true
        }
    };

    const superGroupMap = {};
    for (let i = 0; i < superGroups.length; i++) {
        superGroupMap[superGroups[i].id] = superGroups[i].prettyName;
    }

    componentData[SUPER_GROUP] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.SuperGroup,
            valueToTextMap: superGroupMap,
            filled: true
        }
    };

    return componentData;
}

const Groups = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    const [superGroups, setSuperGroups] = useState([]);

    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    useEffect(() => {
        getSuperGroups().then(response => {
            setSuperGroups(response.data);
        });
    }, []);

    if (superGroups.length === 0) {
        return null;
    }

    return (
        <DigitCRUD
            name={"groups"}
            path={"/groups"}
            readAllRequest={getGroupsMinified}
            readOneRequest={getGroup}
            updateRequest={editGroup}
            createRequest={addGroup}
            keysOrder={[
                ID,
                NAME,
                EMAIL,
                "descriptionSv",
                "descriptionEn",
                "functionSv",
                "functionEn",
                SUPER_GROUP
            ]}
            keysText={{
                id: text.Id,
                name: text.Name,
                email: text.Email,
                descriptionSv: text.DescriptionSv,
                descriptionEn: text.DescriptionEn,
                functionSv: text.FunctionSv,
                functionEn: text.FunctionEn
            }}
            tableProps={{
                columnsOrder: [ID, NAME, EMAIL],
                orderBy: NAME,
                startOrderBy: NAME
            }}
            formInitialValues={{
                id: "",
                name: "",
                email: "",
                descriptionSv: "",
                descriptionEn: "",
                functionSv: "",
                functionEn: "",
                superGroup: ""
            }}
            formValidationSchema={generateValidationSchema(text)}
            formComponentData={generateEditComponentData(text, superGroups)}
            idProp={"id"}
        />
    );
};

export default Groups;
