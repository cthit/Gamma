import React, { useEffect } from "react";
import {
    EMAIL,
    NAME,
    PRETTY_NAME,
    TYPE,
    TYPE_BOARD,
    TYPE_COMMITTEE,
    TYPE_SOCIETY
} from "../../api/super-groups/props.super-groups.api";
import * as yup from "yup";
import {
    DigitSelect,
    DigitTextField,
    useDigitTranslations,
    DigitCRUD
} from "@cthit/react-digit-components";
import translations from "./SuperGroups.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    getSuperGroup,
    getSuperGroups
} from "../../api/super-groups/get.super-groups.api";
import { addSuperGroup } from "../../api/super-groups/post.super-groups.api";
import { deleteSuperGroup } from "../../api/super-groups/delete.super-groups.api";
import { editSuperGroup } from "../../api/super-groups/put.super-groups.api";

function generateValidationSchema(text) {
    const schema = {};
    schema[NAME] = yup.string().required();
    schema[PRETTY_NAME] = yup.string().required();
    schema[TYPE] = yup.string().required();

    return yup.object().shape(schema);
}

function generateEditComponentData(text) {
    const componentData = {};

    componentData[NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            outlined: true
        }
    };

    componentData[PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            outlined: true
        }
    };

    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            outlined: true
        }
    };

    const typeValueToTextMap = {};
    typeValueToTextMap[TYPE_SOCIETY] = text.Society;
    typeValueToTextMap[TYPE_COMMITTEE] = text.Committee;
    typeValueToTextMap[TYPE_BOARD] = text.Board;

    componentData[TYPE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Type,
            valueToTextMap: typeValueToTextMap,
            outlined: true
        }
    };

    return componentData;
}

const SuperGroups = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    return (
        <DigitCRUD
            name={"superGroups"}
            path={"/super-groups"}
            readAllRequest={getSuperGroups}
            readOneRequest={getSuperGroup}
            createRequest={addSuperGroup}
            deleteRequest={deleteSuperGroup}
            updateRequest={editSuperGroup}
            keysOrder={[NAME, PRETTY_NAME, TYPE, EMAIL]}
            keysText={{
                name: text.Name,
                prettyName: text.PrettyName,
                type: text.Type,
                email: text.Email
            }}
            formComponentData={generateEditComponentData(text)}
            formInitialValues={{
                name: "",
                prettyName: "",
                type: TYPE_SOCIETY,
                email: ""
            }}
            formValidationSchema={generateValidationSchema(text)}
            tableProps={{
                titleText: text.SuperGroups,
                search: true
            }}
            idProp={"id"}
        />
    );
};

export default SuperGroups;
