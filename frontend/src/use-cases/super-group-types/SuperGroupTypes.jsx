import React from "react";

import translations from "./SuperGroupTypes.translations.json";
import { useDigitTranslations, DigitCRUD } from "@cthit/react-digit-components";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "../../common/views/insufficient-access";

import {
    initialValues,
    keysComponentData,
    keysOrder,
    keysText,
    validationSchema
} from "./SuperGroupTypes.options";
import {
    getSuperGroupTypes,
    getSuperGroupTypeUsage
} from "../../api/super-group-types/get.super-group-types.api";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import DisplayGroupsTable from "../../common/elements/display-groups-table/DisplayGroupsTable.element";
import { addSuperGroupType } from "../../api/super-group-types/post.super-group-types.api";
import { deleteSuperGroupType } from "../../api/super-group-types/delete.super-group-types.api";

const SuperGroupTypes = ({}) => {
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            formComponentData={keysComponentData(text)}
            formValidationSchema={validationSchema(text)}
            keysText={keysText(text)}
            readOneKeysOrder={[]}
            formInitialValues={initialValues()}
            keysOrder={keysOrder}
            path={"/super-group-types"}
            name={"super-group-types"}
            readAllRequest={getSuperGroupTypes}
            readOneRequest={getSuperGroupTypeUsage}
            createRequest={addSuperGroupType}
            deleteRequest={deleteSuperGroupType}
            tableProps={{
                titleText: "Supergroup types",
                startOrderBy: "type",
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            idProp={"type"}
            useHistoryGoBackOnBack
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            detailsTitle={one => one.type}
            detailsCustomRender={(one, goBack) => (
                <DisplayGroupsTable
                    title={one.type + " usages"}
                    groups={one.usages}
                    columnsOrder={["prettyName", "name"]}
                    superGroup
                    backButton={{ text: text.Back, onClick: goBack }}
                    margin={{ bottom: "calc(56px + 16px)" }}
                />
            )}
            useKeyTextsInUpperLabel
        />
    );
};

export default SuperGroupTypes;
