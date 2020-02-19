import React, { useEffect } from "react";
import {
    DigitCRUD,
    DigitTextField,
    useDigitTranslations
} from "@cthit/react-digit-components";
import {
    getWhitelist,
    getWhitelistItem
} from "../../api/whitelist/get.whitelist.api";
import { addUsersToWhitelist } from "../../api/whitelist/post.whitelist.api";
import { editWhitelistItem } from "../../api/whitelist/put.whitelist.api";
import translations from "./Whitelist.translations";
import * as yup from "yup";
import { deleteWhitelistItem } from "../../api/whitelist/delete.whitelist.api";
import useIsAdmin from "../../common/hooks/use-is/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";

const Whitelist = () => {
    const [text] = useDigitTranslations(translations);

    const admin = useIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            name={"whitelist"}
            path={"/whitelist"}
            readAllRequest={getWhitelist}
            readOneRequest={getWhitelistItem}
            createRequest={data => addUsersToWhitelist({ cids: [data.cid] })}
            updateRequest={editWhitelistItem}
            deleteRequest={deleteWhitelistItem}
            keysText={{ id: text.Id, cid: text.Cid }}
            keysOrder={["id", "cid"]}
            readAllKeysOrder={["cid"]}
            formComponentData={{
                cid: {
                    component: DigitTextField,
                    componentProps: {
                        outlined: true,
                        maxLength: 12
                    }
                }
            }}
            formValidationSchema={yup.object().shape({
                cid: yup.string().required()
            })}
            formInitialValues={{
                cid: ""
            }}
            idProp={"id"}
            tableProps={{
                titleText: text.Whitelist,
                startOrderBy: "cid",
                search: true
            }}
            toastCreateSuccessful={data =>
                data.cid + " " + text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingWhitelist}
            toastDeleteSuccessful={data =>
                data.cid + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data => text.FailedDeleting + " " + data.cid}
            toastUpdateSuccessful={data =>
                data.cid + " " + text.WasUpdatedSuccessfully
            }
            toastUpdateFailed={data =>
                text.WhitelistUpdateFailed1 +
                " " +
                data.cid +
                " " +
                text.WhitelistUpdateFailed2
            }
            createTitle={text.SaveCidToWhitelist}
            createButtonText={text.AddWhitelist}
            updateTitle={data => text.Update + " " + data.cid}
            updateButtonText={data => text.Update + " " + data.cid}
            backButtonText={text.Back}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteDescription={data =>
                text.WouldYouLikeToDelete + " " + data.cid + "?"
            }
            detailsTitle={data => data.cid}
            deleteButtonText={data => text.Delete + " " + data.cid}
            detailsButtonText={text.Details}
        />
    );
};

export default Whitelist;
