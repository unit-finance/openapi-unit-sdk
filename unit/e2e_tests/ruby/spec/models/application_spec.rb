# frozen_string_literal: true

require 'openapi_client'
require_relative '../spec_helper'
require 'json'
require 'date'

RSpec.describe 'Application' do
  before do
    # run before each test
    configure_tests
  end

  describe 'test attribute "id"' do
    let(:client) { OpenapiClient::ApiClient.new(configuration) }

    it 'should create an application' do
      api_instance = OpenapiClient::CreateApplicationApi.new(client)
      request = { data: OpenapiClient::CreateIndividualApplication.new(type: 'individualApplication',
                                                                       attributes: OpenapiClient::CreateIndividualApplicationAttributes.new(
                                                                         ssn: '123456789', full_name: OpenapiClient::FullName.new({ first: 'John', last: 'Kenn' }), date_of_birth: Date.new(1989, 2, 1), address: ADDRESS, email: EMAIL, phone: PHONE,
                                                                         occupation: 'ArchitectOrEngineer', annual_income: 'UpTo10k', source_of_income: 'EmploymentOrPayrollIncome'
                                                                       )).to_hash }
      response = api_instance.execute(request)
      expect(response.data.type).to eq('individualApplication')
    end

    it 'should upload a document for the application' do
      api_instance = OpenapiClient::UploadADocumentForAnApplicationApi.new(client)
      request = api_instance.execute('836683', '125214', get_document_contents)

      expect(request.data.type).to eq('document')
    end

    it 'should upload a document for the application' do
      api_instance = OpenapiClient::UploadADocumentForAnApplicationApi.new(client)
      request = api_instance.execute('836683', '125215', get_image_contents('./spec/picture.png'))
      expect(request.data.type).to eq('document')
    end

    it 'should upload a document for the application' do
      api_instance = OpenapiClient::UploadADocumentForAnApplicationApi.new(client)
      request = api_instance.execute('836683', '125216', get_image_contents('./spec/check1.jpg'))
      expect(request.data.type).to eq('document')
    end
  end
end
