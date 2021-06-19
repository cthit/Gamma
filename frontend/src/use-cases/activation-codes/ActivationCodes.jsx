import React from "react";

import { useDigitTranslations, DigitCRUD } from "@cthit/react-digit-components";

import { deleteActivationCode } from "api/activation-codes/delete.activationCodes.api";
import {
    getActivationCode,
    getActivationCodes
} from "api/activation-codes/get.activationCodes.api";
import {
    AC_CID,
    AC_NAME
} from "api/activation-codes/props.activationCodes.api";

import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import FiveZeroZero from "../../app/elements/five-zero-zero";
import FourOFour from "../four-o-four";
import { keysOrder, keysText } from "./ActivationCodes.options";
import translations from "./ActivationCodes.translations";

const ActivationCodes = () => {
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            keysOrder={keysOrder()}
            name={"activationCodes"}
            path={"/activation-codes"}
            readAllRequest={getActivationCodes}
            readOneRequest={getActivationCode}
            deleteRequest={deleteActivationCode}
            tableProps={{
                titleText: text.ActivationCodes,
                startOrderBy: AC_CID,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            keysText={keysText(text)}
            idProp={AC_CID}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.WouldYouLikeToDelete + " " + data[AC_CID]
            }
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteCancel={() => text.Cancel}
            toastDeleteSuccessful={data =>
                data[AC_CID] + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.ActivationCodeDeletionFailed1 +
                " " +
                data[AC_NAME] +
                " " +
                text.ActivationCodeDeletionFailed2
            }
            detailsTitle={data => data[AC_CID]}
            deleteButtonText={data => text.Delete + " " + data[AC_CID]}
            detailsButtonText={text.Details}
            backButtonText={text.Back}
            dateAndTimeProps={["createdAt"]}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
        />
    );
};

export default ActivationCodes;
