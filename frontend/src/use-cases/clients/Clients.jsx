import {
    DigitCRUD,
    useDigitTranslations,
    DigitTextField,
    DigitTextArea,
    DigitText,
    DigitButton,
    useDigitCustomDialog,
    useGammaIsAdmin
} from "@cthit/react-digit-components";
import React, { useEffect } from "react";
import { getClient, getClients } from "../../api/clients/get.clients.api";
import { addClient } from "../../api/clients/post.clients.api";
import translations from "./Clients.translations";
import * as yup from "yup";
import { deleteClient } from "../../api/clients/delete.clients.api";
import InsufficientAccess from "../../common/views/insufficient-access";
import { CLIENT_NAME } from "../../api/clients/props.clients.api";

const Clients = () => {
    const [openDialog] = useDigitCustomDialog();
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            name={"clients"}
            path={"/clients"}
            readAllRequest={getClients}
            readOneRequest={getClient}
            deleteRequest={deleteClient}
            createRequest={client =>
                new Promise((resolve, reject) =>
                    addClient({
                        name: client.name,
                        description: {
                            sv: client.descriptionSv,
                            en: client.descriptionEn
                        },
                        webServerRedirectUri: client.webServerRedirectUri
                    })
                        .then(response => {
                            openDialog({
                                title: text.YourClientSecret,
                                onConfirm: () => {},
                                renderMain: () => (
                                    <DigitText.Text
                                        text={response.data.clientSecret}
                                    />
                                ),
                                renderButtons: confirm => (
                                    <DigitButton
                                        text={text.CloseDialog}
                                        onClick={confirm}
                                    />
                                )
                            });
                            resolve(response);
                        })
                        .catch(error => reject(error))
                )
            }
            tableProps={{
                titleText: text.Clients,
                startOrderBy: "name",
                search: true
            }}
            keysOrder={[
                "id",
                "name",
                "webServerRedirectUri",
                "descriptionSv",
                "descriptionEn"
            ]}
            keysText={{
                id: text.Id,
                name: text.Name,
                webServerRedirectUri: text.RedirectURI,
                descriptionSv: text.DescriptionSv,
                descriptionEn: text.DescriptionEn
            }}
            idProp={"id"}
            formValidationSchema={yup.object().shape({
                webServerRedirectUri: yup.string().required(),
                name: yup.string().required(),
                descriptionSv: yup.string().required(),
                descriptionEn: yup.string().required()
            })}
            formInitialValues={{
                webServerRedirectUri: "",
                name: "",
                descriptionSv: "",
                descriptionEn: ""
            }}
            formComponentData={{
                webServerRedirectUri: {
                    component: DigitTextField,
                    componentProps: {
                        upperLabel: text.RedirectURI,
                        outlined: true,
                        maxLength: 100
                    }
                },
                name: {
                    component: DigitTextField,
                    componentProps: {
                        upperLabel: text.Name,
                        outlined: true,
                        maxLength: 50
                    }
                },
                descriptionSv: {
                    component: DigitTextArea,
                    componentProps: {
                        upperLabel: text.DescriptionSv,
                        outlined: true,
                        rows: 3,
                        maxRows: 5,
                        maxLength: 500
                    }
                },
                descriptionEn: {
                    component: DigitTextArea,
                    componentProps: {
                        upperLabel: text.DescriptionEn,
                        outlined: true,
                        rows: 3,
                        maxRows: 5,
                        maxLength: 500
                    }
                }
            }}
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
        />
    );
};

export default Clients;
