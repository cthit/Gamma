import React, { useEffect, useState } from "react";

import {
    DigitCRUD,
    useDigitTranslations,
    DigitText,
    DigitButton,
    useDigitCustomDialog,
    DigitDesign,
    DigitLoading,
    useDigitDialog
} from "@cthit/react-digit-components";

import { deleteClient } from "api/clients/delete.clients.api";
import { getClient, getClients } from "api/clients/get.clients.api";
import { addClient, resetClientSecret } from "api/clients/post.clients.api";
import {
    CLIENT_NAME,
    CLIENT_SECRET,
    CLIENT_ID
} from "api/clients/props.clients.api";

import useGammaIsAdmin from "common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import InsufficientAccess from "common/views/insufficient-access";

import FiveZeroZero from "../../app/elements/five-zero-zero";
import FourOFour from "../four-o-four";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    readAllKeysOrder,
    createKeysOrder,
    keysText,
    validationSchema
} from "./Clients.options";
import translations from "./Clients.translations";
import UserClientApprovals from "./views/user-client-approvals";
import { getAuthorityLevels } from "../../api/authorities/get.authorities";
import ClientRestrictions from "./client-restrictions";

const Clients = () => {
    const [text] = useDigitTranslations(translations);
    const [openCustomDialog] = useDigitCustomDialog();
    const [openDialog] = useDigitDialog();
    const [authorityLevels, setAuthorityLevels] = useState(null);

    useEffect(() => {
        getAuthorityLevels().then(response =>
            setAuthorityLevels(response.data)
        );
    }, []);

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    if (authorityLevels == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    return (
        <DigitCRUD
            keysOrder={keysOrder()}
            createKeysOrder={createKeysOrder()}
            readAllKeysOrder={readAllKeysOrder()}
            keysText={keysText(text)}
            formValidationSchema={validationSchema(text)}
            formInitialValues={initialValues()}
            formComponentData={keysComponentData(text, authorityLevels)}
            name={"clients"}
            path={"/clients"}
            readAllRequest={getClients}
            readOneRequest={getClient}
            deleteRequest={deleteClient}
            createRequest={client =>
                new Promise((resolve, reject) =>
                    addClient(client)
                        .then(response => {
                            openCustomDialog({
                                title: text.YourClientSecret,
                                renderButtons: confirm => (
                                    <DigitButton
                                        text={text.Close}
                                        onClick={confirm}
                                    />
                                ),
                                renderMain: () => (
                                    <>
                                        <DigitText.Text
                                            text={
                                                "Secret: " +
                                                response.data[
                                                    CLIENT_SECRET
                                                ].replace("{noop}", "")
                                            }
                                        />
                                        {response.data.apiKeyToken != null && (
                                            <>
                                                <DigitDesign.Divider />
                                                <DigitText.Text
                                                    text={
                                                        "Api token: " +
                                                        response.data
                                                            .apiKeyToken
                                                    }
                                                />
                                            </>
                                        )}
                                        <DigitDesign.Divider />
                                        <DigitText.Text
                                            text={
                                                text.YourClientSecretDescription
                                            }
                                        />
                                    </>
                                )
                            });
                            resolve(response);
                        })
                        .catch(error => reject(error))
                )
            }
            tableProps={{
                titleText: text.Clients,
                startOrderBy: CLIENT_NAME,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            idProp={CLIENT_ID}
            createTitle={text.CreateClient}
            createButtonText={text.CreateClient}
            detailsTitle={data => data[CLIENT_NAME]}
            toastCreateSuccessful={data =>
                data[CLIENT_NAME] + " " + text.ClientCreatingSuccessful
            }
            toastCreateFailed={() => text.ErrorCreatingClient}
            toastDeleteSuccessful={data =>
                data[CLIENT_NAME] + " " + text.ClientDeletionSuccessful
            }
            toastDeleteFailed={data =>
                text.ClientDeletionFailed1 +
                " " +
                data[CLIENT_NAME] +
                " " +
                text.ClientDeletionFailed2
            }
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.AreYouSureYouWantToDelete + " " + data[CLIENT_NAME]
            }
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteCancel={() => text.Cancel}
            backButtonText={text.Back}
            detailsButtonText={text.Details}
            deleteButtonText={data => text.Delete + " " + data[CLIENT_NAME]}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            useKeyTextsInUpperLabel
            detailsRenderCardEnd={client => (
                <DigitButton
                    text={text.UpdateClientSecret}
                    outlined
                    onClick={() => {
                        openDialog({
                            title: text.AreYouSure,
                            description: text.ResetClient,
                            confirmButtonText: text.UpdateClientSecret,
                            cancelButtonText: text.Cancel,
                            onConfirm: () =>
                                resetClientSecret(client.clientId).then(
                                    response =>
                                        openCustomDialog({
                                            title: text.YourClientSecret,
                                            renderButtons: confirm => (
                                                <DigitButton
                                                    text={text.Close}
                                                    onClick={confirm}
                                                />
                                            ),
                                            renderMain: () => (
                                                <>
                                                    <DigitText.Text
                                                        text={
                                                            "Secret: " +
                                                            response.data.replace(
                                                                "{noop}",
                                                                ""
                                                            )
                                                        }
                                                    />
                                                </>
                                            )
                                        })
                                )
                        });
                    }}
                />
            )}
            detailsRenderEnd={client => (
                <>
                    {client.restrictions != null &&
                        client.restrictions.length > 0 && (
                            <ClientRestrictions client={client} />
                        )}
                    <UserClientApprovals client={client} />
                </>
            )}
            readOneProps={{
                margin: {
                    bottom: "16px"
                }
            }}
        />
    );
};

export default Clients;
