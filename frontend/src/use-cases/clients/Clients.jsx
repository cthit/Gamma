import {
    DigitCRUD,
    useDigitTranslations,
    DigitText,
    DigitButton,
    useDigitCustomDialog
} from "@cthit/react-digit-components";
import React from "react";
import { getClient, getClients } from "../../api/clients/get.clients.api";
import { addClient } from "../../api/clients/post.clients.api";
import translations from "./Clients.translations";
import { deleteClient } from "../../api/clients/delete.clients.api";
import InsufficientAccess from "../../common/views/insufficient-access";
import {
    CLIENT_DESCRIPTION_ENGLISH,
    CLIENT_DESCRIPTION_SWEDISH,
    CLIENT_NAME,
    CLIENT_REDIRECT,
    CLIENT_SECRET,
    CLIENT_ID
} from "../../api/clients/props.clients.api";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    keysText,
    validationSchema
} from "./Clients.options";

const Clients = () => {
    const [text] = useDigitTranslations(translations);
    const [openDialog] = useDigitCustomDialog({
        title: text.YourClientSecret,
        renderButtons: confirm => (
            <DigitButton text={text.Close} onClick={confirm} />
        )
    });

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            keysOrder={keysOrder()}
            keysText={keysText(text)}
            formValidationSchema={validationSchema(text)}
            formInitialValues={initialValues()}
            formComponentData={keysComponentData()}
            name={"clients"}
            path={"/clients"}
            readAllRequest={getClients}
            readOneRequest={getClient}
            deleteRequest={deleteClient}
            createRequest={client =>
                new Promise((resolve, reject) =>
                    addClient({
                        name: client[CLIENT_NAME],
                        description: {
                            sv: client[CLIENT_DESCRIPTION_SWEDISH],
                            en: client[CLIENT_DESCRIPTION_ENGLISH]
                        },
                        webServerRedirectUri: client[CLIENT_REDIRECT]
                    })
                        .then(response => {
                            openDialog({
                                renderMain: () => (
                                    <>
                                        <DigitText.Text
                                            bold
                                            alignCenter
                                            text={response.data[
                                                CLIENT_SECRET
                                            ].replace("{noop}", "")}
                                        />
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
                padding: "0px"
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
        />
    );
};

export default Clients;
