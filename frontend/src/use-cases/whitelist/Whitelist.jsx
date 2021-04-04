import React from "react";
import { DigitCRUD, useDigitTranslations } from "@cthit/react-digit-components";
import {
    getWhitelist,
    getWhitelistItem
} from "../../api/whitelist/get.whitelist.api";
import { addUsersToWhitelist } from "../../api/whitelist/post.whitelist.api";
import { editWhitelistItem } from "../../api/whitelist/put.whitelist.api";
import translations from "./Whitelist.translations";
import { deleteWhitelistItem } from "../../api/whitelist/delete.whitelist.api";
import InsufficientAccess from "../../common/views/insufficient-access";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    keysText,
    validationSchema
} from "./Whitelist.options";
import { WHITELIST_CID } from "../../api/whitelist/props.whitelist.api";

const Whitelist = () => {
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            keysText={keysText(text)}
            name={"whitelist"}
            path={"/whitelist"}
            readAllRequest={getWhitelist}
            createRequest={data =>
                addUsersToWhitelist({ cids: [data[WHITELIST_CID]] })
            }
            deleteRequest={deleteWhitelistItem}
            keysOrder={keysOrder()}
            formComponentData={keysComponentData()}
            formValidationSchema={validationSchema(text)}
            formInitialValues={initialValues()}
            idProp={WHITELIST_CID}
            tableProps={{
                titleText: text.Whitelist,
                startOrderBy: WHITELIST_CID,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            toastCreateSuccessful={data =>
                data[WHITELIST_CID] + " " + text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingWhitelist}
            toastDeleteSuccessful={data =>
                data[WHITELIST_CID] + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.FailedDeleting + " " + data[WHITELIST_CID]
            }
            createTitle={text.SaveCidToWhitelist}
            createButtonText={text.AddWhitelist}
            backButtonText={text.Back}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteDescription={data =>
                text.WouldYouLikeToDelete + " " + data[WHITELIST_CID] + "?"
            }
            detailsTitle={data => data.cid}
            deleteButtonText={data => text.Delete + " " + data[WHITELIST_CID]}
            detailsButtonText={text.Details}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            useKeyTextsInUpperLabel
            useHistoryGoBackOnBack
        />
    );
};

export default Whitelist;
