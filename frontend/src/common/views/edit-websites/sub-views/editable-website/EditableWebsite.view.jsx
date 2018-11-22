import {
    DigitIconButton,
    DigitLayout,
    DigitText,
    DigitTextField,
    DigitTooltip,
    DigitTranslations
} from "@cthit/react-digit-components";
import { Cancel, Delete, Edit, Save } from "@material-ui/icons";
import React from "react";
import translations from "./EditableWebsite.view.translations.json";

class EditableWebsite extends React.Component {
    state = {
        editing: false,
        newUrl: this.props.url
    };
    render() {
        const { prettyWebsite, url, website, onChange, onDelete } = this.props;
        const { editing, newUrl } = this.state;
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Common.Views.EditWebsites.SubView.EditableWebsite"
                render={text => (
                    <DigitLayout.Column justifyContent="space-between">
                        <DigitLayout.Column>
                            <DigitLayout.Size minWidth="150px">
                                <DigitText.Text text={prettyWebsite} />
                            </DigitLayout.Size>
                            <DigitLayout.Spacing />
                            <DigitLayout.Hide hidden={editing}>
                                <DigitText.Text text={url} />
                            </DigitLayout.Hide>
                            <DigitLayout.Hide hidden={!editing}>
                                <DigitTextField
                                    upperLabel={text.Url}
                                    value={newUrl}
                                    onChange={e => {
                                        this.setState({
                                            newUrl: e.target.value
                                        });
                                    }}
                                />
                            </DigitLayout.Hide>
                        </DigitLayout.Column>
                        <DigitLayout.Column justifyContent="flex-end">
                            <DigitLayout.Hide hidden={editing}>
                                <DigitTooltip text={text.Edit}>
                                    <DigitIconButton
                                        icon={Edit}
                                        onClick={() =>
                                            this.setState({
                                                editing: true
                                            })
                                        }
                                    />
                                </DigitTooltip>
                            </DigitLayout.Hide>
                            <DigitLayout.Hide hidden={!editing}>
                                <DigitTooltip text={text.Cancel}>
                                    <DigitIconButton
                                        icon={Cancel}
                                        onClick={() =>
                                            this.setState({
                                                editing: false,
                                                newUrl: url
                                            })
                                        }
                                    />
                                </DigitTooltip>
                                <DigitTooltip text={text.Save}>
                                    <DigitIconButton
                                        icon={Save}
                                        onClick={() => {
                                            this.setState({
                                                editing: false
                                            });
                                            onChange({
                                                website: website,
                                                url: this.state.newUrl
                                            });
                                        }}
                                    />
                                </DigitTooltip>
                            </DigitLayout.Hide>
                            <div>
                                <DigitTooltip text={text.Delete}>
                                    <DigitIconButton
                                        icon={Delete}
                                        onClick={() => onDelete()}
                                    />
                                </DigitTooltip>
                            </div>
                        </DigitLayout.Column>
                    </DigitLayout.Column>
                )}
            />
        );
    }
}

export default EditableWebsite;
