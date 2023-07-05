# frozen_string_literal: true

require 'openapi_client'
require_relative "../../../../spec/spec_helper.rb"
require 'json'
require 'date'


RSpec.describe 'Application' do
  before do
    # run before each test
    configure_tests
  end


  describe 'test attribute "id"' do
    let(:client) { OpenapiClient::ApiClient.new(configuration) }


    it 'should upload a document for the application' do
      api_instance = OpenapiClient::UploadADocumentForAnApplicationApi.new(client)
      request = api_instance.execute("836683", "125214", get_document_contents)
      expect(request.data.type).to eq("document")
    end

    it 'should upload a document for the application' do
      api_instance = OpenapiClient::UploadADocumentForAnApplicationApi.new(client)
      request = api_instance.execute("836683", "125214", get_image_contents("./spec/picture.png"))
      expect(request.data.type).to eq("document")
    end

    it 'should upload a document for the application' do
      api_instance = OpenapiClient::UploadADocumentForAnApplicationApi.new(client)
      request = api_instance.execute("836683", "125215", get_image_contents("./spec/check1.jpg"))
      expect(request.data.type).to eq("document")
    end
  end
end
