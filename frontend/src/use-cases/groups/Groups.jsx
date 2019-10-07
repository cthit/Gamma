import React, { useEffect } from "react";

import { DigitCRUD, useDigitTranslations } from "@cthit/react-digit-components";
import translations from "./Groups.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { getGroupsMinified } from "../../api/groups/get.groups.api";
import {
    DESCRIPTION,
    EMAIL,
    FUNCTION,
    ID,
    NAME
} from "../../api/groups/props.groups.api";

const Groups = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    return (
        <DigitCRUD
            name={"groups"}
            path={"/groups"}
            readAllRequest={getGroupsMinified}
            keysOrder={[ID, NAME, DESCRIPTION, EMAIL, FUNCTION]}
            keysText={{
                id: text.Id,
                name: text.Name,
                description: text.Description,
                email: text.Email,
                function: text.Function
            }}
            tableProps={{
                columnsOrder: [ID, NAME, DESCRIPTION, EMAIL, FUNCTION],
                orderBy: NAME,
                startOrderBy: NAME
            }}
            idProp={"id"}
        />
    );
};

export default Groups;
